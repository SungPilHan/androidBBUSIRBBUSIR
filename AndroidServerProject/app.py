#-*-coding:utf-8-*-
import getopt
from flask.wrappers import Response
from sqlalchemy.sql.expression import or_
import web
import sys
from cheroot import wsgi
from flask import Flask, request, request_started, render_template_string, render_template
from functools import wraps
from models import History, User, Account
from database import db_session
import simplejson as json
makejson = json.dumps
app = Flask(__name__)
makejson = json.dumps

DEFAULT_PORT_NO = 8888

def usageguide():
	print("InsecureBankv2 Backend-Server")
	print("Options: ")
	print("  --port p    serve on port p (default 8888)")
	print("  --help      print(this message")

@app.errorhandler(500)
def internal_servererror(error):
	print(" [!]", error)
	return "Internal Server Error", 500

'''
The function handles the authentication mechanism
'''
@app.route('/login', methods=['POST'])
def login():
	Responsemsg="fail"
	user = request.form['username']
	#checks for presence of user in the database #requires models.py
	u = User.query.filter(User.username == user).first()
	print("u=",u)
	if u.username == "admin" and "admin"==request.form["password"]:
		Responsemsg="Welcome admin"
	elif u and u.password == request.form["password"]:
		Responsemsg="Correct Credentials"
	elif u and u.password != request.form["password"]:
		Responsemsg="Wrong Password"
	elif not u:
		Responsemsg="User Does not Exist"
	else: Responsemsg="Some Error"
	data = {"message" : Responsemsg, "user": user}
	print(makejson(data))
	return makejson(data)

@app.route('/signup', methods=['GET', 'POST'])
def signup():
    return render_template('signup.html')

@app.route('/idcheck', methods=['POST'])
def idcheck():
	Responsemsg="User Exist"
	username = request.form["username"]
	u = User.query.filter(User.username == username).first()
	if not u:
		Responsemsg="You Can Use It"	
	data = {"message" : Responsemsg}
	print(makejson(data))
	return makejson(data)	

@app.route('/join', methods=['POST'])
def join():
	Responsemsg="Error"
	username = request.form["username"]
	password = request.form["password"]
	try:
		db_session.add(User(username=username, password=password))
		db_session.commit()
		Responsemsg="Done"
	except:
		pass
	data = {"message" : Responsemsg}
	print(makejson(data))
	accountadd()
	return makejson(data)	

'''
The function responds back with the from and to debit accounts corresponding to logged in user
'''
'''@app.route('/getaccounts', methods=['POST'])
def getaccounts():
	#set accounts from the request 
	Responsemsg="fail"
	acc1=acc2=from_acc=to_acc=0
	user=request.form['username']
	#checks for presence of user in the database
	u = User.query.filter(User.username == user).first()
	if not u or u.password != request.form["password"]:
		Responsemsg="Wrong Credentials so trx fail"
	else:
		Responsemsg="Correct Credentials so get accounts will continue"
		a=Account.query.filter(Account.user == user)
		for i in a:
			if (i.type=='from'):
				from_acc=i.account_number;
		for j in a:
			if (i.type=='to'):
				to_acc=i.account_number;
	data = {"message" : Responsemsg, "from": from_acc,"to": to_acc}
	print(makejson(data))
	return makejson(data)
'''
'''
The function takes a new password as input and passes it on to the change password module
'''
@app.route('/changepassword', methods=['POST'])
def changepassword(): 
	Responsemsg="fail"
	newpassword=request.form['newpassword']
	user=request.form['username']
	print(newpassword)
	u = User.query.filter(User.username == user).first()
	if not u:
		Responsemsg="Error"
	else:
		try:
			u.password = newpassword
			db_session.commit()
			Responsemsg="Change Password Successful"
		except:
			Responsemsg="Error"
	data = {"message" : Responsemsg}
	print(makejson(data))
	return makejson(data)
	
'''
The function handles the transaction module
'''
@app.route('/dotransfer', methods=['POST'])
def dotransfer():
	Responsemsg="fail"
	user=request.form['username']
	amount=request.form['amount']
	u = User.query.filter(User.username == user).first()
	if not u or u.password != request.form["password"]:
		Responsemsg="Wrong Credentials so trx fail"
		data = {"message" : Responsemsg}
	else:
		Responsemsg="Success"
		from_acc = request.form["from_acc"]
		to_acc = request.form["to_acc"]
		amount = request.form["amount"]
		from_account = Account.query.filter(Account.accountNo == from_acc).first()
		to_account = Account.query.filter(Account.accountNo == to_acc).first()
		try:
			to_account.balance += int(amount)
			from_account.balance -= int(amount)
			db_session.add(History(int(from_acc), int(to_acc), int(amount)))
			db_session.commit()
			data = {"message" : Responsemsg, "from": from_acc, "to": to_acc,  "amount": amount}
		except:
			data = {"message" : "Error"}
	print(makejson(data))
	return makejson(data)

#사용자 계좌 목록 조회 기능
@app.route('/accountlist', methods=['POST'])
def accountlist(): 
	Responsemsg="fail"
	username=request.form['username']
	u = Account.query.filter(Account.user == username).all()
	if not u:
		Responsemsg="Error"
		data = {"message" : Responsemsg}
	else:
		Responsemsg="Success"
		data = {"message" : Responsemsg, "account" : [{"number" : i.accountNo} for i in u]}	
	print(makejson(data))
	return makejson(data)

#계좌 추가 기능
@app.route('/accountadd', methods=['POST'])
def accountadd(): 
	Responsemsg="fail"
	username=request.form['username']
	try:
		db_session.add(Account(balance=100000, user=username))
		db_session.commit()
		account = Account.query.filter(Account.user == username).order_by(Account.accountNo.desc()).first()
		Responsemsg="Success"
		data = {"message" : Responsemsg, "account" : account.accountNo}
	except:
		data = {"message" : Responsemsg}
	print(makejson(data))
	return makejson(data)

#이체 내역 조회 기능
@app.route('/searchtransferhistory', methods=['GET','POST'])
def searchtransferhistory():
	account=int(request.form['account'])
	u = History.query.filter(or_(History.from_acc==account, History.to_acc==account)).order_by(History.id.desc()).all()
	balance = Account.query.filter(Account.accountNo==account).first().balance
	historylist=[]
	amount = 0
	for i in u:
		if i.from_acc==account:
			balance += amount
			js = {"display_acc" : i.to_acc, "amount" : -(i.amount), "balance" : balance}
		else:
			balance -= amount
			js = {"display_acc" : i.from_acc, "amount" : i.amount, "balance" : balance}
		amount = i.amount
		historylist.append(js)
	print(historylist)
	return render_template('searchtransferhistory.html', historylist=historylist)

'''
The function provides login mechanism to a developer user during development phase
'''
'''
@app.route('/devlogin', methods=['POST'])
def devlogin():
	user=request.form['username']
	Responsemsg="Correct Credentials"
	data = {"message" : Responsemsg, "user": user}
	print(makejson(data))
	return makejson(data)
'''

'''
SSTI
'''
@app.route('/info', methods=['GET'])
def info():
	info = request.args.get('info') or None
	temp = '''
		{}
	'''.format(info)
	print(info)
	return render_template_string(temp)

# admin page
@app.route('/admin', methods=['GET', 'POST'])
def admin():
    return render_template('admin.html')
	

if __name__ == '__main__':
	port = DEFAULT_PORT_NO
	options, args = getopt.getopt(sys.argv[1:], "", ["help", "port="])
	for op, arg1 in options:
		if op == "--help":
			usageguide()
			sys.exit(2)
		elif op == "--port":
			port = int(arg1)

	urls = ("/.*", "app")
	apps = web.application(urls, globals())
	server = wsgi.Server(("0.0.0.0", port),app,server_name='localhost')
	print("The server is hosted on port:",(port))
	
	try:
		server.start()
	#apps.run(port)
	except KeyboardInterrupt:
		server.stop()
