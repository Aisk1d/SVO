import zlib, struct, random

ROOT = "/home/claude/mod/src/main"
ENT = ROOT + "/resources/assets/frontline/textures/entity/"
ITEM = ROOT + "/resources/assets/frontline/textures/item/"


def clampc(v):
    return max(0, min(255, int(v)))


def png(path, px):
    h = len(px)
    w = len(px[0])
    raw = b''.join(b'\x00' + b''.join(bytes(c) for c in row) for row in px)

    def ch(t, d):
        c = struct.pack('>I', len(d)) + t + d
        return c + struct.pack('>I', zlib.crc32(t + d) & 0xffffffff)

    open(path, 'wb').write(b'\x89PNG\r\n\x1a\n' + ch(b'IHDR', struct.pack('>IIBBBBB', w, h, 8, 6, 0, 0, 0))
                            + ch(b'IDAT', zlib.compress(raw)) + ch(b'IEND', b''))


def paint(img, x0, y0, w, h, color, rng):
    for yy in range(y0, y0 + h):
        for xx in range(x0, x0 + w):
            n = rng.randint(-6, 6)
            col = tuple(clampc(c + n) for c in color)
            if w >= 3 and h >= 3 and (xx in (x0, x0 + w - 1) or yy in (y0, y0 + h - 1)):
                col = tuple(clampc(c * 0.78) for c in col)
            img[yy][xx] = col + (255,)


# (u, v, dx, dy, dz) — совпадает с боксами в MissileModel.createBodyLayer
BOXES = [
    ("body", 0, 0, 28, 6, 6),
    ("nose1", 0, 14, 4, 5, 5),
    ("nose2", 0, 26, 3, 3, 3),
    ("nose3", 0, 34, 2, 1, 1),
    ("nozzle", 0, 38, 3, 4, 4),
    ("finV", 20, 14, 7, 14, 1),
    ("finH", 40, 14, 7, 1, 14),
]


def faces(u, v, dx, dy, dz):
    return [(u + dz, v, dx, dz), (u + dz + dx, v, dx, dz), (u, v + dz, dz, dy),
            (u + dz, v + dz, dx, dy), (u + dz + dx, v + dz, dz, dy), (u + 2 * dz + dx, v + dz, dx, dy)]


def make(name, palette, seed):
    W, H = 128, 64
    img = [[(0, 0, 0, 0)] * W for _ in range(H)]
    rng = random.Random(seed)
    for pname, u, v, dx, dy, dz in BOXES:
        color = palette.get(pname, palette["default"])
        for (fx, fy, fw, fh) in faces(u, v, dx, dy, dz):
            paint(img, fx, fy, fw, fh, color, rng)
    png(ENT + name + ".png", img)


def make_icon(name, main_color, accent):
    W = H = 16
    img = [[(0, 0, 0, 0)] * W for _ in range(H)]
    rng = random.Random(hash(name) & 0xffff)
    # простой силуэт ракеты по диагонали
    for y in range(3, 13):
        x0 = max(1, y - 3)
        x1 = min(14, y + 2)
        for x in range(x0, x1):
            c = accent if (x + y) % 5 == 0 else main_color
            n = rng.randint(-6, 6)
            img[y][x] = tuple(clampc(v + n) for v in c) + (255,)
    png(ITEM + name + ".png", img)


# "Орешник" — тёмный гранит/графит корпус с красными боевыми отметками, крупная ракета средней дальности
oreshnik_palette = {
    "default": (46, 46, 50),
    "body": (44, 44, 48),
    "nose1": (30, 30, 33),
    "nose2": (24, 24, 26),
    "nose3": (18, 18, 20),
    "nozzle": (60, 40, 40),
    "finV": (150, 30, 30),
    "finH": (150, 30, 30),
}
make("oreshnik_missile", oreshnik_palette, 111)
make_icon("oreshnik_missile", (44, 44, 48), (150, 30, 30))

# "Буревестник" — вытянутый низкопрофильный корпус, хаки/оливковый камуфляж крылатой ракеты
burevestnik_palette = {
    "default": (92, 98, 74),
    "body": (96, 102, 76),
    "nose1": (60, 64, 50),
    "nose2": (48, 52, 40),
    "nose3": (30, 32, 26),
    "nozzle": (40, 40, 42),
    "finV": (70, 76, 58),
    "finH": (70, 76, 58),
}
make("burevestnik_missile", burevestnik_palette, 222)
make_icon("burevestnik_missile", (96, 102, 76), (60, 64, 50))

print("готово")
