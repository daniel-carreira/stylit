from flask import Flask, jsonify, request
from flask_cors import CORS
from flask_rq2 import RQ
from model.stable_diffusion_wrapper import StableDiffusionV2
import uuid

app = Flask(__name__)
app.config["RQ_REDIS_URL"] = "redis://localhost:6379/0"
app.static_folder = "public"
app.static_url_path = "/assets"

cors = CORS(app, resources={r"/*": {"origins": "*"}})
rq = RQ(app)

stable_diff_model = StableDiffusionV2()

@rq.job
def generate_images_task(text_prompt, num_images):

    images = stable_diff_model.generate_images(text_prompt, num_images)
    images_urls = []

    for image in images:
        filename = f"{uuid.uuid1()}.jpg"
        image.save(filename)
        images_urls.append(f"http://localhost:8080/assets/{filename}")
        
    return images_urls

@app.route("/v1/images/generations", methods=["POST"])
def generate_images_api():
    json_data = request.get_json(force=True)
    text_prompt = json_data["text"]
    num_images = json_data["num_images"]

    task = generate_images_task.queue(text_prompt, num_images)
    return jsonify({"task_id": task.id})

@app.route("/v1/images/tasks/<task_id>", methods=["GET"])
def get_task_status(task_id):
    task = rq.get_task(task_id)
    if task.is_finished:
        # Task is finished, retrieve the generated images
        images = task.result
        return jsonify({"status": "finished", "images": images})
    elif task.is_queued:
        return jsonify({"status": "queued"})
    elif task.is_started:
        return jsonify({"status": "started"})
    else:
        return jsonify({"status": "unknown"})
    
if __name__ == '__main__':
    app.run(host='0.0.0.0')
