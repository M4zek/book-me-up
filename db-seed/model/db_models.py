from argparse import FileType
from datetime import datetime
from enum import Enum as PyEnum
from typing import List, Optional

from sqlalchemy import ForeignKey, String, Integer, Text, Boolean, Double, DateTime, BigInteger, func, \
    Float, Enum
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column, relationship


class FileType(PyEnum):
    IMG_USER_AVATAR = "IMG_USER_AVATAR",
    IMG_COMPANY_LOGO = "IMG_COMPANY_LOGO",
    IMG_COMPANY_PORTFOLIO = "IMG_COMPANY_PORTFOLIO"


class Base(DeclarativeBase):
    pass



class UserRole(Base):
    __tablename__ = "user_roles"
    user_id: Mapped[int] = mapped_column(ForeignKey("users.id"), primary_key=True)
    role_id: Mapped[int] = mapped_column(ForeignKey("roles.id"), primary_key=True)


class CompanyUserRole(Base):
    __tablename__ = "company_user_roles"
    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    user_id: Mapped[int] = mapped_column(ForeignKey("users.id", ondelete="CASCADE"))
    company_id: Mapped[int] = mapped_column(ForeignKey("companies.id", ondelete="CASCADE"))
    company_role_id: Mapped[int] = mapped_column(ForeignKey("company_roles.id"))

    company: Mapped["Company"] = relationship(back_populates="staff_links")




class Category(Base):
    __tablename__ = "categories"
    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    name: Mapped[Optional[str]] = mapped_column(String(255))
    created_date: Mapped[datetime] = mapped_column(server_default=func.now())
    modified_date: Mapped[datetime] = mapped_column(server_default=func.now(), onupdate=func.now())

    companies: Mapped[List["Company"]] = relationship(back_populates="category")


class Company(Base):
    __tablename__ = "companies"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(Text)
    description: Mapped[str] = mapped_column(Text)
    average_rating: Mapped[float] = mapped_column(Float)
    category_id: Mapped[int] = mapped_column(ForeignKey("categories.id"))
    created_date: Mapped[datetime] = mapped_column(server_default=func.now())
    modified_date: Mapped[datetime] = mapped_column(server_default=func.now(), onupdate=func.now())

    category: Mapped["Category"] = relationship(back_populates="companies")
    address: Mapped["Address"] = relationship(back_populates="company", cascade="all, delete-orphan")
    hours: Mapped[List["CompanyHour"]] = relationship(back_populates="company", cascade="all, delete-orphan")
    offers: Mapped[List["CompanyOffer"]] = relationship(back_populates="company", cascade="all, delete-orphan")

    images: Mapped[List["StoredFile"]] = relationship(back_populates="company", cascade="all, delete-orphan")

    staff_links: Mapped[List["CompanyUserRole"]] = relationship(
        back_populates="company",
        cascade="all, delete-orphan"
    )

class StoredFile(Base):
    __tablename__ = "stored_file"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    object_key: Mapped[str] = mapped_column(String(255))
    original_file_name: Mapped[str] = mapped_column(String(255))
    content_type: Mapped[str] = mapped_column(String(255))
    size: Mapped[int] = mapped_column(Integer)
    type: Mapped[FileType] = mapped_column(Enum(FileType))

    company_id: Mapped[Optional[int]] = mapped_column(ForeignKey("companies.id"))
    user_data_id: Mapped[Optional[int]] = mapped_column(ForeignKey("users_data.id"), unique=True)

    company: Mapped[Optional["Company"]] = relationship(back_populates="images")
    user_data: Mapped[Optional["UserData"]] = relationship(back_populates="avatar")



class Address(Base):
    __tablename__ = "addresses"
    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    city: Mapped[Optional[str]] = mapped_column(String(255))
    postal_code: Mapped[Optional[str]] = mapped_column(String(255))
    street: Mapped[str] = mapped_column(String(255))
    building_number: Mapped[Optional[str]] = mapped_column(String(255))
    company_id: Mapped[int] = mapped_column(ForeignKey("companies.id", ondelete="CASCADE"))
    created_date: Mapped[datetime] = mapped_column(server_default=func.now())
    modified_date: Mapped[datetime] = mapped_column(server_default=func.now(), onupdate=func.now())

    company: Mapped["Company"] = relationship(back_populates="address")



class UserData(Base):
    __tablename__ = "users_data"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    first_name: Mapped[Optional[str]] = mapped_column(String(255))
    last_name: Mapped[Optional[str]] = mapped_column(String(255))
    date_of_birth: Mapped[datetime] = mapped_column(DateTime)
    phone_number: Mapped[Optional[str]] = mapped_column(String(255))
    created_date: Mapped[datetime] = mapped_column(server_default=func.now())

    user: Mapped["User"] = relationship(back_populates="user_data")

    avatar: Mapped[Optional["StoredFile"]] = relationship(back_populates="user_data", cascade="all, delete-orphan")

class User(Base):
    __tablename__ = "users"
    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    address_email: Mapped[str] = mapped_column(String(255), unique=True)
    password: Mapped[str] = mapped_column(String(255))
    user_data_id: Mapped[int] = mapped_column(ForeignKey("users_data.id", ondelete="CASCADE"))
    is_block: Mapped[bool] = mapped_column(Boolean, default=False)
    is_enable: Mapped[bool] = mapped_column(Boolean, default=True)

    user_data: Mapped["UserData"] = relationship(back_populates="user")
    roles: Mapped[List["Role"]] = relationship(secondary="user_roles", back_populates="users")
    refresh_tokens: Mapped[List["RefreshToken"]] = relationship(back_populates="user")
    messages: Mapped[List["Message"]] = relationship(back_populates="author")


class Role(Base):
    __tablename__ = "roles"
    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(255), unique=True)
    users: Mapped[List["User"]] = relationship(secondary="user_roles", back_populates="roles")

    def __str__(self):
        return f"{self.name}"

class CompanyRole(Base):
    __tablename__ = "company_roles"
    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(255))


class CompanyOffer(Base):
    __tablename__ = "company_offers"
    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(Text)
    description: Mapped[str] = mapped_column(Text)
    price: Mapped[float] = mapped_column(Double)
    duration: Mapped[int] = mapped_column(Integer)
    company_id: Mapped[Optional[int]] = mapped_column(ForeignKey("companies.id", ondelete="CASCADE"))

    company: Mapped["Company"] = relationship(back_populates="offers")
    reservations: Mapped[List["Reservation"]] = relationship(back_populates="offer")
    reviews: Mapped[List["Review"]] = relationship(back_populates="offer")


class Reservation(Base):
    __tablename__ = "reservations"
    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    reservation_date: Mapped[datetime] = mapped_column(DateTime)
    reservation_number: Mapped[Optional[str]] = mapped_column(String(255), unique=True)
    status: Mapped[str] = mapped_column(String(20), server_default="PENDING")
    user_id: Mapped[int] = mapped_column(ForeignKey("users.id", ondelete="CASCADE"))
    company_offer_id: Mapped[int] = mapped_column(ForeignKey("company_offers.id", ondelete="CASCADE"))
    preferred_user_id: Mapped[Optional[int]] = mapped_column(ForeignKey("users.id", ondelete="SET NULL"))

    offer: Mapped["CompanyOffer"] = relationship(back_populates="reservations")


class Review(Base):
    __tablename__ = "reviews"
    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    comment: Mapped[str] = mapped_column(Text)
    rating: Mapped[Optional[int]] = mapped_column(Integer)
    user_id: Mapped[int] = mapped_column(ForeignKey("users.id", ondelete="CASCADE"))
    company_offer_id: Mapped[int] = mapped_column(ForeignKey("company_offers.id", ondelete="CASCADE"))

    offer: Mapped["CompanyOffer"] = relationship(back_populates="reviews")


class CompanyHour(Base):
    __tablename__ = "companies_hours"
    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    day_of_week: Mapped[Optional[str]] = mapped_column(String(255))
    open_time: Mapped[Optional[str]] = mapped_column(String(255))
    close_time: Mapped[Optional[str]] = mapped_column(String(255))
    is_open: Mapped[bool] = mapped_column(Boolean)
    company_id: Mapped[Optional[int]] = mapped_column(ForeignKey("companies.id", ondelete="CASCADE"))

    company: Mapped["Company"] = relationship(back_populates="hours")


class Room(Base):
    __tablename__ = "room"
    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    name: Mapped[Optional[str]] = mapped_column(String(255))
    type: Mapped[str] = mapped_column(String(50))

    messages: Mapped[List["Message"]] = relationship(back_populates="room")


class Message(Base):
    __tablename__ = "message"
    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    content: Mapped[str] = mapped_column(Text)
    message_type: Mapped[str] = mapped_column(String(50))
    room_id: Mapped[int] = mapped_column(ForeignKey("room.id"))
    user_id: Mapped[int] = mapped_column(ForeignKey("users.id"))

    room: Mapped["Room"] = relationship(back_populates="messages")
    author: Mapped["User"] = relationship(back_populates="messages")


class LoginHistory(Base):
    __tablename__ = "login_history"
    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    user_id: Mapped[Optional[int]] = mapped_column(Integer)
    email: Mapped[Optional[str]] = mapped_column(String(255))
    ip_address: Mapped[Optional[str]] = mapped_column(String(255))
    user_agent: Mapped[Optional[str]] = mapped_column(String(255))
    success: Mapped[bool] = mapped_column(Boolean)
    failure_reason: Mapped[Optional[str]] = mapped_column(String(255))
    created_date: Mapped[datetime] = mapped_column(server_default=func.now())


class RefreshToken(Base):
    __tablename__ = "refresh_token"
    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    expire_date: Mapped[datetime] = mapped_column(DateTime)
    refresh_token: Mapped[str] = mapped_column(String(255))
    user_id: Mapped[int] = mapped_column(ForeignKey("users.id"))

    user: Mapped["User"] = relationship(back_populates="refresh_tokens")