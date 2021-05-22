import os
import base64

#from datetime import datetime
from sqlalchemy import Column, Integer, String
from requests import get
ip = get("https://api.ipify.org").text
if(ip=="3.20.202.177"):
    from database_mysql import db_session, Base
else:
	from database import db_session, Base
#import settings

class User(Base):
    __tablename__ = 'users'
    username = Column(String(50), primary_key=True, unique=True)
    password = Column(String(50))
    
    def __init__(self, username=None, password=None):
        self.username = username
        self.password = password

    def __repr__(self):
        return '<User %r>' % (self.username)
    
    @property
    def values(self):
        return {"username" : self.username}

class Account(Base):
    __tablename__ = 'accounts'
    accountNo = Column(Integer, primary_key=True, unique=True) 
    balance = Column(Integer)
    user = Column(String(50))

    def __init__(self, accountNo=None, balance=None, user=None):
        self.accountNo = accountNo
        self.balance = balance
        self.user = user

    def __repr__(self):
        return '<Account %r>' % (self.accountNo)  

    @property
    def values(self):
        return {"accountNo" : self.accountNo,
                "balance" : self.balance,
                "user" : self.user
                }    

class History(Base):
    __tablename__ = 'history'
    id = Column(Integer, primary_key=True, unique=True) 
    from_acc = Column(Integer)
    to_acc = Column(Integer)
    amount = Column(Integer)

    def __init__(self, from_acc=None, to_acc=None, amount=None):
        self.from_acc = from_acc
        self.to_acc = to_acc
        self.amount = amount

    def __repr__(self):
        return '<History %r>' % (self.from_acc)  

    @property
    def values(self):
        return {"from_acc" : self.from_acc,
                "to_acc" : self.to_acc,
                "amount" : self.amount
                }