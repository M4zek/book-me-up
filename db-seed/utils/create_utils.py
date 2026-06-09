import random
import sys
import os
import psutil

from sqlalchemy import select
from sqlalchemy.exc import SQLAlchemyError

from database import Session
from model.db_models import Category, User, Role
from utils.data_fake import create_company, create_day_of_week, create_offer, create_review, create_owner, \
    create_employee_in_company, create_address, create_portfolio_Image


def add_category(name: str):

    session = Session()

    try:
        new_category = Category(name=name)

        session.add(new_category)

        session.commit()

        session.refresh(new_category)

        sys.stdout.write(f"\r\033[K🔄 Category [{new_category.id}] has been add '{new_category.name}'")
        return new_category

    except SQLAlchemyError as e:
        session.rollback()
        sys.stdout.write(f"\r\033[K❌ Error: {e}")
        return None
    finally:
        Session.remove()



def add_user(user: User):
    session = Session()
    try:
        role_user = session.execute(select(Role).where(Role.id == 2)).scalars().one()
        user.roles.append(role_user)
        session.add(user)
        session.commit()
        sys.stdout.write(f"\r\033[K🔄 User [{user.id}] [{user.user_data.first_name} {user.user_data.last_name}] has been added | {get_ram_usage()}")

    except SQLAlchemyError as e:
        session.rollback()
        sys.stdout.write(f"\r\033[K❌ Error [{user.user_data.first_name} {user.user_data.last_name}]")
        return None
    finally:
        Session.remove()


def add_complex_company(user_ids):
    session = Session() 

    try:

        # 1. Create main company object
        new_company = create_company()

        # 2. Adding company business hours (Monday - Sunday)
        days = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
        for day in days:
            hour_record = create_day_of_week(day)
            new_company.hours.append(hour_record)

        # 3. Adding random num of offers (e.g. From 2 to 5)
        num_offers = random.randint(10, 20)
        num_of_reviews = 0
        average_rating = 0

        for i in range(num_offers):
            offer = create_offer()

            # 4. Add for each offer random num of review (From 4 to 8)
            num_reviews = random.randint(4, 8)

            for j in range(num_reviews):
                review_owner = random.choice(user_ids)
                review = create_review(review_owner)
                offer.reviews.append(review)
                num_of_reviews += 1
                average_rating += review.rating

            # Add offer include their reviews into company
            new_company.offers.append(offer)

        # Add average rating based on reviews
        new_company.average_rating = average_rating / num_of_reviews

        # 5. Create employees
        random_owner = random.choice(user_ids)
        owner = create_owner(company_id=new_company.id, owner_id=random_owner)
        new_company.staff_links.append(owner)

        num_employees = random.randint(2, 5)
        for i in range(num_employees):
            random_employee = random.choice(user_ids)
            employee = create_employee_in_company(company_id=new_company.id, employee_id=random_employee)
            new_company.staff_links.append(employee)

        # 6. Create address
        address = create_address()
        new_company.address = address

        # 7. Create company portfolio
        num_portfolios = random.randint(2, 4)
        for i in range(num_portfolios):
            random_img = create_portfolio_Image()
            new_company.portfolio_images.append(random_img)

        # 8. Saved all into database
        session.add(new_company)
        session.commit()
        # session.expunge_all()

        sys.stdout.write(f"\r\033[K🔄 Company [{new_company.id}] {new_company.name} with {len(new_company.offers)} offers has been add | {get_ram_usage()}")
        return new_company

    except SQLAlchemyError as e:
        Session.rollback()
        sys.stdout.write(f"\r\033[K❌ Error during add company: {e}")
        return None
    finally:
        Session.remove()



def read_all_users_ids():
    session = Session()
    try:
        ALL_USER_IDS = session.execute(
            select(User.id)
        ).scalars().all()
    except SQLAlchemyError as e:
        return None

    return ALL_USER_IDS
    

def get_ram_usage():
    process = psutil.Process(os.getpid())
    ram_mb = process.memory_info().rss / 1024 / 1024
    return (f"RAM: {ram_mb:.2f} MB")