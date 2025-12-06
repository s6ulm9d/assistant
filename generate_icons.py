import os
from PIL import Image, ImageDraw

source_image_path = r"C:/Users/soulmad/.gemini/antigravity/brain/cb9aa094-16d2-4d92-8e97-8ffce6b9242e/uploaded_image_1764129524708.jpg"
res_dir = r"app/src/main/res"

sizes = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192
}

def create_round_icon(img, size):
    mask = Image.new('L', (size, size), 0)
    draw = ImageDraw.Draw(mask)
    draw.ellipse((0, 0, size, size), fill=255)
    
    output = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    
    # Resize image to fit
    img_resized = img.resize((size, size), Image.Resampling.LANCZOS)
    
    output.paste(img_resized, (0, 0), mask=mask)
    return output

def main():
    if not os.path.exists(source_image_path):
        print(f"Error: Source image not found at {source_image_path}")
        return

    try:
        img = Image.open(source_image_path)
        # Convert to RGBA to handle transparency if needed, though source is jpg
        img = img.convert("RGBA")
        
        # Center crop to square if needed
        width, height = img.size
        new_dim = min(width, height)
        left = (width - new_dim)/2
        top = (height - new_dim)/2
        right = (width + new_dim)/2
        bottom = (height + new_dim)/2
        img = img.crop((left, top, right, bottom))

        for folder, size in sizes.items():
            folder_path = os.path.join(res_dir, folder)
            if not os.path.exists(folder_path):
                os.makedirs(folder_path)
            
            # Standard icon
            icon = img.resize((size, size), Image.Resampling.LANCZOS)
            icon.save(os.path.join(folder_path, "ic_launcher.png"))
            
            # Round icon
            round_icon = create_round_icon(img, size)
            round_icon.save(os.path.join(folder_path, "ic_launcher_round.png"))
            
            print(f"Generated icons for {folder}")
            
    except Exception as e:
        print(f"An error occurred: {e}")

if __name__ == "__main__":
    main()
