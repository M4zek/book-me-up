import io
import os

from minio import Minio

from utils.image_utils import ImageSize, resize_images

BUCKET_NAME = "app-storage"

# Default locale variable
ENDPOINT = os.environ.get("MINIO_URL",
                              default="localhost:9000")

ACCESS_KEY = os.environ.get("MINIO_ACCESS_KEY", default="minioadmin")
SECRET_KEY = os.environ.get("MINIO_SECRET_KEY", default="minioadmin")
SECURE = os.environ.get("MINIO_SECURE", default=False)

minio_client = Minio(
    ENDPOINT,
    access_key=ACCESS_KEY,
    secret_key=SECRET_KEY,
    secure=False,
)

def create_bucket():
    if not minio_client.bucket_exists(BUCKET_NAME):
        minio_client.make_bucket(BUCKET_NAME)

def save_images(image, object_key):
    create_bucket()

    images = resize_images(image)

    for size in ImageSize:
        img = images[size]
        key = object_key.replace(".webp", f'[{size.value}].webp')

        img_stream = io.BytesIO(img)
        file_length = len(img)

        minio_client.put_object(
            BUCKET_NAME,
            key,
            img_stream,
            file_length,
            content_type="image/webp"
        )
