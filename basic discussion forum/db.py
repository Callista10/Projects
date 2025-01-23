'''
db
database file, containing all the logic to interface with the sql database
'''

from sqlalchemy import create_engine
from sqlalchemy.orm import Session
from models import *
import secrets
import hashlib

# "database/main.db" specifies the database file
# change it if you wish
# turn echo = True to display sql output
engine = create_engine("sqlite:///database/main.db", echo=False)

# initializes the database
Base.metadata.create_all(engine)

def hash_password(password, salt):
    if salt is None:
        salt = secrets.token_hex(16)
    else:
        salt = salt.decode("utf-8")
    salted_password = salt.encode() + password.encode()
    hash_object = hashlib.sha256()
    hash_object.update(salted_password)
    hashed_password = hash_object.hexdigest()
    return hashed_password, salt

# inserts a user to the database
def insert_user(username: str, password: str, salt):
    with Session(engine) as session:
        user = User(username=username, password=password, salt=salt)
        session.add(user)
        session.commit()

# gets a user from the database
def get_user(username: str):
    with Session(engine) as session:
        return session.get(User, username)
