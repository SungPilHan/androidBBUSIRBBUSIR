import frida, sys
import argparse

def on_message(message, date):
    if message['type'] == 'send':
        print(message['payload'])
    elif message['type'] == 'error':
        print(message['stack'])

def get_message_from_js(message, data):
    print(message['payload'])

def get_script(script_name):
    with open("./"+script_name, 'r') as f:
        script = f.read()
    return script

help_script = """
HELP
"""

parser = argparse.ArgumentParser(description=help_script)
parser.add_argument('--script', required=True, help='JS File to Inject')
args = parser.parse_args()

device=frida.get_usb_device()

p1=device.spawn(["com.android.BBUSIRBBUSIR"])

process_session=device.attach(p1)

script = process_session.create_script(get_script(args.script))
script.on('message', on_message)
script.load()

device.resume(p1)

sys.stdin.read()