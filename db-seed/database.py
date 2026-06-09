from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, scoped_session


DATABASE_URL = "mysql+pymysql://root:root@db:3306/book_me_up"

engine = create_engine(
    DATABASE_URL,
    connect_args={"init_command": "SET time_zone='+00:00'"},
    pool_size=10,
    max_overflow=20,
    pool_recycle=3600
)

session_factory = sessionmaker(bind=engine)

Session = scoped_session(session_factory)
