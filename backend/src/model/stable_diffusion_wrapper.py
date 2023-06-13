from diffusers import StableDiffusionPipeline, EulerDiscreteScheduler
import torch

class StableDiffusionV2:
    def __init__(self) -> None:
        model_id = "stabilityai/stable-diffusion-2-base"
        scheduler = EulerDiscreteScheduler.from_pretrained(model_id, subfolder="scheduler")
        pipe = StableDiffusionPipeline.from_pretrained(model_id, scheduler=scheduler, torch_dtype=torch.float16)
        pipe = pipe.to("cuda")

    def generate_images(self, text_prompt: str, num_images: int):
        prompt = [text_prompt] * num_images
        images = self.pipe(prompt, num_inference_steps=10).images
        return images