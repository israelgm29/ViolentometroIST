#!/bin/bash

cd src/assets/images

for input in image*.png; do
    # Skip if it is already a circle file
    if [[ "$input" == *"-circle.png" ]]; then
        continue
    fi

    # Construct output name: image11.png -> image11-circle.png
    filename=$(basename "$input" .png)
    output="${filename}-circle.png"
    
    echo "Processing $input -> $output"
    
    # Crop to square (center), then mask with circle
    magick "$input" \
        -gravity center \
        -crop '1:1' \
        +repage \
        -alpha set \
        \( +clone -alpha transparent -fill white -draw 'circle %[fx:w/2],%[fx:h/2] %[fx:w/2],0' \) \
        -compose DstIn \
        -composite \
        "$output"
done

echo "Done processing images."
ls -lh *-circle.png
