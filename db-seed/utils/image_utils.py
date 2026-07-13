import io
from enum import Enum
from typing import Dict
from PIL import Image


class ImageSize(Enum):
    SMALL = "SMALL"
    MEDIUM = "MEDIUM"
    LARGE = "LARGE"
    ORIGINAL = "ORIGINAL"


def resize_images(file_bytes: bytes, file_format: str = "PNG") -> Dict[ImageSize, bytes]:
    result = {}

    original_image = Image.open(io.BytesIO(file_bytes))

    sizes = {
        ImageSize.SMALL: (300, 300),
        ImageSize.MEDIUM: (500, 500),
        ImageSize.LARGE: (1000, 1000)
    }


    for size_enum, dimensions in sizes.items():

        img_copy = original_image.copy()
        img_copy.thumbnail(dimensions, Image.Resampling.LANCZOS)


        img_byte_arr = io.BytesIO()
        img_copy.save(img_byte_arr, format=file_format)
        result[size_enum] = img_byte_arr.getvalue()


    result[ImageSize.ORIGINAL] = file_bytes

    return result