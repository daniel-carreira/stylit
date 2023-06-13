import os
from pathlib import Path
import time

from flask import Flask, jsonify, request, send_from_directory
from flask_cors import CORS
from stable_diffusion import StableDiffusionV2

app = Flask(__name__)
cors = CORS(app, resources={r"/api/v1/images/*": {"origins": "*"}})

print("Starting Server...")

stable_diff_model = None

@app.route("/api/v1/images/generate", methods=["POST"])
def generate_images():
    json_data = request.get_json(force=True)
    text_prompt = json_data["text"]
    num_images = json_data["num_images"]
    generated_imgs = stable_diff_model.generate_images(text_prompt, num_images)

    returned_generated_images = []
    dir_name = "generations"
    Path(dir_name).mkdir(parents=True, exist_ok=True)
    
    for idx, img in enumerate(generated_imgs):
        filename = f"{time.strftime('%Y-%m-%d_%H-%M-%S')}_{idx}.jpeg"
        img.save(os.path.join(dir_name, filename), format='jpeg')
        returned_generated_images.append(filename)

    print(f"Created {num_images} images from text prompt [{text_prompt}]")
    return jsonify({'generations': returned_generated_images})

@app.route('/api/v1/images/<filename>')
def download_file(filename):
    return send_from_directory("generations", filename)

with app.app_context():
    stable_diff_model = StableDiffusionV2()
    print("Server is up and running!")
    
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080, debug=False)
