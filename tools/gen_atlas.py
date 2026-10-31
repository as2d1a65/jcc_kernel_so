from PIL import Image, ImageDraw, ImageFont
import os

W, H = 20, 30          # cell size
COLS = 16
CHARS = ''.join(chr(c) for c in range(32, 127))   # ASCII 32..126 -> 95 glyphs
ROWS = (len(CHARS) + COLS - 1) // COLS

img = Image.new('RGBA', (COLS * W, ROWS * H), (0, 0, 0, 0))
d = ImageDraw.Draw(img)

font = None
for cand in [
    r'C:\Windows\Fonts\consola.ttf',
    r'C:\Windows\Fonts\arial.ttf',
    r'C:\Windows\Fonts\segoeui.ttf',
]:
    if os.path.exists(cand):
        font = ImageFont.truetype(cand, 24)
        print('font:', cand)
        break
if font is None:
    font = ImageFont.load_default()
    print('fallback default font')

for i, ch in enumerate(CHARS):
    cx = (i % COLS) * W
    cy = (i // COLS) * H
    bbox = d.textbbox((0, 0), ch, font=font)
    gw = bbox[2] - bbox[0]
    gh = bbox[3] - bbox[1]
    x = cx + max(0, (W - gw) // 2) - bbox[0]
    y = cy + max(0, (H - gh) // 2) - bbox[1]
    d.text((x, y), ch, font=font, fill=(120, 255, 140, 255))

out = r'D:\zpc\zpc_project_move\crack\android\jcc\.work\ovl\atlas.png'
img.save(out)
print('atlas %s  %dx%d  %d cols x %d rows' % (out, img.width, img.height, COLS, ROWS))
