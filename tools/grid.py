from PIL import Image, ImageDraw

src = r'D:\zpc\zpc_project_move\crack\android\jcc\.work\s3.png'
dst = r'D:\zpc\zpc_project_move\crack\android\jcc\.work\s3_grid.png'

im = Image.open(src).convert('RGB')
W, H = im.size
d = ImageDraw.Draw(im)

step = 200
for x in range(0, W, step):
    d.line([(x, 0), (x, H)], fill=(255, 0, 0), width=2)
    d.text((x + 6, 6), str(x), fill=(255, 255, 0))
    d.text((x + 6, H - 40), str(x), fill=(255, 255, 0))
for y in range(0, H, step):
    d.line([(0, y), (W, y)], fill=(0, 255, 255), width=2)
    d.text((6, y + 2), str(y), fill=(255, 255, 0))
    d.text((W - 90, y + 2), str(y), fill=(255, 255, 0))

im.save(dst)
print('saved', dst, im.size)
