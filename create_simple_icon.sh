#!/bin/bash
# Create simple 1x1 pixel PNG as placeholders
# These will be replaced with proper icons later

for dir in app/src/main/res/mipmap-{m,h,xh,xxh,xxxh}dpi; do
    # Create a simple colored square using ImageMagick if available,
    # otherwise create minimal valid PNG files
    if command -v convert &> /dev/null; then
        convert -size 48x48 xc:'#4A9EFF' "$dir/ic_launcher.png"
        convert -size 48x48 xc:'#4A9EFF' "$dir/ic_launcher_round.png"
    else
        # Create minimal valid PNG (1x1 blue pixel)
        echo -n -e '\x89PNG\r\n\x1a\n\x00\x00\x00\rIHDR\x00\x00\x00\x01\x00\x00\x00\x01\x08\x02\x00\x00\x00\x90wS\xde\x00\x00\x00\x0cIDATx\x9cc\xf8\xcf\xc0\x00\x00\x00\x03\x00\x01\x8e\x1e\xe5\xb8\x00\x00\x00\x00IEND\xaeB`\x82' > "$dir/ic_launcher.png"
        cp "$dir/ic_launcher.png" "$dir/ic_launcher_round.png"
    fi
done
echo "Icon placeholders created!"
