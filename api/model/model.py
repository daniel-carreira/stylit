import torch
from model.generator import Generator

# Initialize Model
model_path = 'model.pt'
generator = Generator(0)
generator.load_state_dict(torch.load(model_path, map_location=torch.device('cpu')))
generator.eval()

# Define fixed noise and label tensor
fixed_noise = torch.randn(100, 100, 1, 1)
label_tensor = torch.zeros(100, 10, 1, 1)

def generate_image(prompt):
    label_tensor[0, int(prompt), :, :] = 1

    with torch.no_grad():
        generated_image = generator(fixed_noise.cpu(), label_tensor.cpu()).numpy()[0].squeeze()

    return generated_image