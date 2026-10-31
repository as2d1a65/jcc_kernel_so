from PIL import Image, ImageDraw
import sys

src = sys.argv[1]
dst = sys.argv[2]
x0, y0, x1, y1 = (int(v) for v in sys.argv[3:7])
scale = int(sys.argv[7]) if len(sys.argv) > 7 else 4

im = Image.open(src).convert('RGB')
crop = im.crop((x0, y0, x1, y1))
crop = crop.resize((crop.width * scale, crop.height * scale), Image.NEAREST)

d = ImageDraw.Draw(crop)
step = 20 * scale
for x in range(0, crop.width, step):
    d.line([(x, 0), (x, crop.height)], fill=(255, 0, 0), width=1)
    d.text((x + 2, 2), str(x0 + x // scale), fill=(255, 255, 0))
for y in range(0, crop.height, step):
    d.line([(0, y), (crop.width, y)], fill=(0, 255, 255), width=1)
    d.text((2, y + 1), str(y0 + y // scale), fill=(255, 255, 0))

crop.save(dst)
print('saved %s  crop(%d,%d)-(%d,%d) scale=%d' % (dst, x0, y0, x1, y1, scale))
