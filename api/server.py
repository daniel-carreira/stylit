from flask import Flask, jsonify, request
import firebase_admin
from firebase_admin import credentials, storage
import numpy as np
from PIL import Image
import io
import uuid

from model import model

# Initialize Flask
app = Flask(__name__)

# Initialize Firebase
cred = credentials.Certificate('service-account.json')
bucket_name = 'stylit-80e8f.appspot.com'
firebase_admin.initialize_app(cred, {'storageBucket': bucket_name})

def upload_image_to_firebase(image_data):
    # Convert the image array to a PIL Image object
    image = Image.fromarray(np.uint8((image_data + 1.0) * 127.5))

    # Create a byte buffer to hold the image data
    image_byte_array = io.BytesIO()

    # Save the image to the byte buffer in JPEG format
    image.save(image_byte_array, format='JPEG')

    # Reset the byte buffer's position to the start
    image_byte_array.seek(0)

    bucket = storage.bucket()
    filename = f'{str(uuid.uuid4())}.jpg'
    blob = bucket.blob(f'images/{filename}')
    blob.upload_from_file(image_byte_array, content_type='image/jpeg')
    #image_url = blob.public_url
    image_url = f"https://firebasestorage.googleapis.com/v0/b/{bucket_name}/o/images%2F{filename}?alt=media"
    return image_url

@app.route('/v1/images', methods=['POST'])
def generate_image_from_prompt():
    prompt = request.json.get('prompt')

    if prompt is None:
        return jsonify({'error': 'Missing prompt'}), 400

    generated_image = model.generate_image(prompt)
    image_url = upload_image_to_firebase(generated_image)

    response = {'image_url': image_url}
    return jsonify(response)

if __name__ == '__main__':
    app.run(debug=True)