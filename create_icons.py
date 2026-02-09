from PIL import Image, ImageDraw

def create_launcher_icon(size, filename):
    # Create image with dark background
    img = Image.new('RGBA', (size, size), (10, 10, 10, 255))
    draw = ImageDraw.Draw(img)
    
    # Draw a simple camera icon
    # Circle for lens
    center = size // 2
    radius = size // 4
    draw.ellipse(
        [(center - radius, center - radius - size//10), 
         (center + radius, center + radius - size//10)],
        fill=(74, 158, 255, 255),
        outline=(255, 255, 255, 255),
        width=size//30
    )
    
    # Camera body
    body_height = size // 5
    draw.rectangle(
        [(size//4, center + radius//2), 
         (size*3//4, center + radius//2 + body_height)],
        fill=(74, 158, 255, 255)
    )
    
    img.save(filename)

# Create icons for different densities
create_launcher_icon(48, 'app/src/main/res/mipmap-mdpi/ic_launcher.png')
create_launcher_icon(72, 'app/src/main/res/mipmap-hdpi/ic_launcher.png')
create_launcher_icon(96, 'app/src/main/res/mipmap-xhdpi/ic_launcher.png')
create_launcher_icon(144, 'app/src/main/res/mipmap-xxhdpi/ic_launcher.png')
create_launcher_icon(192, 'app/src/main/res/mipmap-xxxhdpi/ic_launcher.png')

# Create round icons (same for now)
create_launcher_icon(48, 'app/src/main/res/mipmap-mdpi/ic_launcher_round.png')
create_launcher_icon(72, 'app/src/main/res/mipmap-hdpi/ic_launcher_round.png')
create_launcher_icon(96, 'app/src/main/res/mipmap-xhdpi/ic_launcher_round.png')
create_launcher_icon(144, 'app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png')
create_launcher_icon(192, 'app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png')

print("Icons created successfully!")
