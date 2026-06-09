import threading
import time
from concurrent.futures import ThreadPoolExecutor

from utils.create_utils import add_category, add_user, add_complex_company, read_all_users_ids
from utils.data_fake import create_user

TOTAL_USERS = 5000
TOTAL_COMPANIES = 1000

COMPANY_CATEGORIES = [
    "Barber",
    "Hair Salon",
    "Beauty Salon",
    "Nail Salon",
    "Tattoo Studio",
    "Spa & Wellness",
    "Massage Therapy",
    "Fitness & Gym",
    "Car Wash & Detailing",
    "Pet Grooming",
    "Cleaning Services",
    "Plumbing Services",
    "Electrical Services",
    "Landscaping & Garden",
    "Delivery & Courier Services",
    "Photography Studio",
    "Auto Repair Shop",
    "Computer Repair",
    "Mobile Phone Service",
    "Catering Services"
]

def format_time( seconds) -> str:
    hours = int(seconds // 3600)
    minutes = int((seconds % 3600) // 60)
    secs = int(seconds % 60)
    return f"{hours:02d}:{minutes:02d}:{secs:02d}"

def insert():

    all_time_start = time.time()

    print("Start working...\n")

    # Categories -----------------------------------------------
    category_start_time = time.time()

    for category in COMPANY_CATEGORIES:
        t = threading.Thread(target=add_category, args=(category,))
        t.start()
        t.join()


    current_time = time.time()
    elapsed = current_time - category_start_time
    print("\n✅ [CATEGORY SAVED] Saving in:", format_time(elapsed))

    # Users -----------------------------------------------

    user_start_time = time.time()

    with ThreadPoolExecutor(max_workers=10) as executor:
        executor.map(add_user, 
                     (create_user() for _ in range(TOTAL_USERS)))

    current_time = time.time()
    elapsed = current_time - user_start_time
    print("\n✅ [USER SAVED] Saving in:", format_time(elapsed))


    # Companies -----------------------------------------------
    company_start_time = time.time()

    ALL_USER_IDS = read_all_users_ids()

    with ThreadPoolExecutor(max_workers=10) as executor:
        for _ in range(0,TOTAL_COMPANIES):
            executor.submit(add_complex_company, ALL_USER_IDS)

    current_time = time.time()
    elapsed = current_time - company_start_time
    print("\n✅ [COMPANIES SAVED] Saving in:", format_time(elapsed))


    # Reservation insert -----------------------------------------------
    current_time = time.time()
    elapsed = current_time - all_time_start
    print("\nAll operation in:", format_time(elapsed))


if __name__ == '__main__':
    insert()