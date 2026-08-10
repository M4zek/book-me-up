import os
import random
import uuid
from datetime import datetime, timedelta

from faker import Faker
from faker.decode import unidecode
from minio.replicationconfig import Status

from model.db_models import UserData, User, Company, CompanyHour, CompanyOffer, Review, CompanyUserRole, Address, \
    StoredFile, FileType, UserStatus

fake = Faker("pl_PL")

reviewer_start_id = 2000
reviewer_end_id = 4500

employee_start_id = 1000
employee_end_id = 3000

owner_start_id = 1
owner_end_id = 999




def generate_email(first_name, last_name):
    first_name = remove_polish_chars(first_name)
    last_name = remove_polish_chars(last_name)

    e_mail = random.choice(['abc', 'zxc', 'qwe', 'asd', 'qaz'])
    return f"{first_name.lower()}.{last_name.lower()}@{e_mail}.com"

def remove_polish_chars(text):
    return unidecode((text))

def read_random_image():
    files = os.listdir("images")

    non_avatar_files = [f for f in files if "avatar" not in f.lower()]

    if not non_avatar_files:
        return None

    random_file = random.choice(non_avatar_files)

    file_path = os.path.join("images", random_file)

    with open(file_path, "rb") as f:
        image_bytes = f.read()

    return image_bytes


def read_random_avatar():
    files = os.listdir("images")
    avatar_files = [f for f in files if "avatar" in f.lower()]
    if not avatar_files:
        return None


    random_file = random.choice(avatar_files)

    file_path = os.path.join("images", random_file)

    with open(file_path, "rb") as f:
        image_bytes = f.read()

    return image_bytes

def random_birthdate(min_age=18, max_age=65):
    today = datetime.today()

    start_date = today.replace(year=today.year - max_age)
    end_date = today.replace(year=today.year - min_age)

    random_days = random.randint(0, (end_date - start_date).days)
    random_birth = start_date + timedelta(days=random_days)

    return random_birth


def create_user_data():
    first_name = fake.first_name()
    last_name = fake.last_name()
    date_of_birth = random_birthdate()
    phone_number = fake.phone_number()

    user_data = UserData(
        first_name=first_name,
        last_name=last_name,
        date_of_birth=date_of_birth,
        phone_number=phone_number
    )
    return user_data

def get_random_status() -> UserStatus:
    ranges = [
        (UserStatus.ACTIVE, 98),
        (UserStatus.SUSPENDED, 1),
        (UserStatus.BLOCK,1)
    ]

    return random.choices(
        ranges,
        weights=[r[1] for r in ranges],
        k=1
    )[0][0]

def random_date_in_future() -> datetime:
    random_days = random.randint(300, 400)

    return datetime.now() + timedelta(days=random_days)

def random_created_date() -> datetime:
    ranges = [
        (0, 30, 0.25),
        (31, 90, 0.25),
        (91, 180, 0.45),
        (181, 365, 0.5),
    ]

    selected_range = random.choices(
        ranges,
        weights=[r[2] for r in ranges],
        k=1
    )[0]

    min_days, max_days, _ = selected_range

    random_days = random.randint(min_days, max_days)

    return datetime.now() - timedelta(days=random_days)

def create_user():
    user_data = create_user_data()

    email = generate_email(first_name=user_data.first_name, last_name=user_data.last_name)
    password = "$2a$10$PVkQ4ffxidufAlppUz8nAOXje8.OkgUSgStuLL/HRy2jnb41ZnLum" # Default -> Password1!

    user_status = get_random_status()
    suspended_to = random_date_in_future() if user_status == UserStatus.SUSPENDED else None
    user = User(
        address_email=email,
        password=password,
        status=user_status,
        suspended_to=suspended_to,
        user_data=user_data,
        created_date=random_created_date()
    )

    return user

def create_stored_file_cmp_portfolio(company_id):
    file_name = f'{fake.word()}.webp'

    file_uuid = remove_polish_chars(f'{uuid.uuid4()}-{file_name}').replace(" ", "")

    content_type = "image/webp"
    size = random.randint(10000000, 2000000000)

    object_key = f'companies/{company_id}/portfolio/{file_uuid}'

    return StoredFile(
        object_key=object_key,
        original_file_name=file_name,
        content_type=content_type,
        size=size,
        type=FileType.IMG_COMPANY_PORTFOLIO
    )

def create_stored_file_cmp_logo(company_id):
    file_name = f'{fake.word()}.webp'

    file_uuid = remove_polish_chars(f'{uuid.uuid4()}-{file_name}').replace(" ", "")

    content_type = "image/webp"
    size = random.randint(10000000, 2000000000)

    object_key = f'companies/{company_id}/logo/{file_uuid}'

    return StoredFile(
        object_key=object_key,
        original_file_name=file_name,
        content_type=content_type,
        size=size,
        type=FileType.IMG_COMPANY_LOGO
    )

def create_stored_file_avatar(user_id):
    file_name = f'{fake.word()}.webp'

    file_uuid = remove_polish_chars(f'{uuid.uuid4()}-{file_name}').replace(" ", "")
    content_type = "image/webp"
    size = random.randint(10000000, 2000000000)

    object_key = f'users/{user_id}/avatar/{file_uuid}'

    return StoredFile(
        object_key=object_key,
        original_file_name=file_name,
        content_type=content_type,
        size=size,
        type=FileType.IMG_USER_AVATAR
    )

def create_company():
    name = fake.company()
    description = fake.paragraph(nb_sentences=random.randint(12, 20))
    category_id = random.randint(1, 20)

    return Company(
        name=name,
        description=description,
        category_id=category_id,
    )


def create_day_of_week(day: str):
    open = random.choices([True, False], weights=[80,20])[0]
    start_time = f"{random.randint(6, 10):02}:{random.choice([0, 10, 20, 30, 40, 50]):02}"
    end_time = f"{random.randint(16, 20):02}:{random.choice([0, 10, 20, 30, 40, 50]):02}"

    return CompanyHour(
        day_of_week=day,
        open_time=start_time,
        close_time=end_time,
        is_open=open,
    )


def create_offer():
    name = fake.sentence(nb_words=5)
    description = fake.paragraph(nb_sentences=random.randint(5, 10))
    price = random.choice([15, 20, 25, 30, 35, 40, 55, 60, 70, 80, 90, 100])
    duration = random.choice([10, 15, 20, 25, 30, 35, 40, 45, 50, 60, 70, 80, 90])
    return CompanyOffer(
        name=name,
        description=description,
        price=price,
        duration=duration,
    )


def create_review(reviewer_id):
    comment = fake.paragraph(nb_sentences=random.randint(1,5))
    rating = random.randint(1,5)
    return Review(
        comment=comment,
        rating=rating,
        user_id=reviewer_id,
    )

def create_owner(company_id, owner_id):
    role_id = 3
    return CompanyUserRole(
        user_id=owner_id,
        company_id=company_id,
        company_role_id=role_id,
    )


def create_employee_in_company(company_id, employee_id):
    role_id = random.choices([1,2], weights=[75,25])
    return CompanyUserRole(
        user_id=employee_id,
        company_id=company_id,
        company_role_id=role_id,
    )


def create_address():
    city = fake.city()
    street = fake.street_name()
    building_number = fake.building_number()
    postal_code = fake.postalcode()
    return Address(
        city=city,
        postal_code=postal_code,
        street=street,
        building_number=building_number,
    )