import random, zlib, struct

ROOT = "/home/claude/mod/src/main"
TEXDIR = ROOT + "/resources/assets/frontline/textures/entity/"
JAVA = ROOT + "/java/com/frontline/client/Models.java"

def B(o, d, c, pat=None): return {"o": o, "d": d, "c": c, "pat": pat}
def P(name, pos=(0, 0, 0), rot=(0, 0, 0), boxes=()): return {"name": name, "pos": pos, "rot": rot, "boxes": list(boxes)}

# ---------- модели (координаты в пикселях, 16 px = 1 блок; x — вперёд, y — вверх, z — вбок) ----------
def fpv_drone():
    dark, black = (38, 40, 44), (26, 26, 30)
    body = [B((-5,-2,-3),(10,4,6),dark), B((-3,2,-2),(6,2,4),(30,30,34)),
            B((5,-1,-2),(3,3,4),(24,24,27)), B((8,0,-1),(1,1,2),(70,130,170)),
            B((-3,-5,-2),(8,3,4),(88,96,66)), B((-4,4,1),(1,4,1),(20,20,20)),
            B((-6,-8,-4),(12,1,1),black), B((-6,-8,3),(12,1,1),black)]
    for sx in (-4, 3):
        for sz in (-4, 3):
            body.append(B((sx,-7,sz),(1,5,1),black))
    for sx in (1, -1):
        for sz in (1, -1):
            body.append(B((sx*9-1,0,sz*9-1),(2,3,2),(60,60,65)))
    parts = [P("body", boxes=body)]
    for i, ang in enumerate((45, -45, 135, -135)):
        parts.append(P("arm%d" % i, rot=(0, ang, 0), boxes=[B((0,-1,-1),(13,2,2),(45,47,52))]))
    rot_pos = [(9,4,-9),(9,4,9),(-9,4,-9),(-9,4,9)]
    for i, p in enumerate(rot_pos):
        parts.append(P("rotor%d" % i, pos=p, boxes=[B((-7,0,-1),(14,1,2),(18,18,20)), B((-1,-1,-1),(2,2,2),(150,150,155))]))
    return parts

def recon_uav():
    w, dk = (212, 214, 218), (60, 64, 70)
    body = [B((-14,-3,-3),(30,6,6),w), B((16,-2,-2),(5,4,4),w), B((21,-1,-1),(3,2,2),dk),
            B((-26,-1,-1),(12,2,2),(200,202,206)), B((-29,-2,-2),(3,4,4),(60,60,65)),
            B((10,-6,-2),(4,3,4),(40,42,48)), B((14,-5,-1),(1,1,2),(70,130,170)),
            B((-2,3,-2),(8,2,4),(190,192,196)), B((6,-4,-4),(3,8,8),(60,110,180))]
    wing = [B((-3,-1,-28),(9,2,28),(224,226,230)), B((-3,-1,0),(9,2,28),(224,226,230)),
            B((-2,1,28),(5,4,1),(60,110,180)), B((-2,1,-29),(5,4,1),(60,110,180))]
    return [P("body", boxes=body), P("wing", boxes=wing),
            P("tailR", pos=(-24,1,0), rot=(35,0,0), boxes=[B((-3,0,-1),(7,12,1),(205,207,211))]),
            P("tailL", pos=(-24,1,0), rot=(-35,0,0), boxes=[B((-3,0,-1),(7,12,1),(205,207,211))]),
            P("prop0", pos=(-30,0,0), boxes=[B((0,-8,-1),(1,16,2),(25,25,28)), B((-1,-1,-1),(2,2,2),(140,140,145))])]

def strike_uav():
    tan, tan2 = (196,180,140), (190,172,130)
    body = [B((-10,-2,-2),(26,4,4),tan), B((16,-2,-2),(5,4,4),(112,118,84)), B((21,-1,-1),(3,2,2),(90,95,66)),
            B((-14,-2,-2),(4,4,4),(64,62,58)), B((12,-3,-3),(2,6,6),(140,35,35)),
            B((-16,-1,-17),(9,2,34),tan2), B((-16,1,16),(7,7,1),(150,132,96)), B((-16,1,-17),(7,7,1),(150,132,96))]
    return [P("body", boxes=body),
            P("wingR", pos=(4,0,0), rot=(0,-50,0), boxes=[B((-6,-1,0),(12,2,26),tan2)]),
            P("wingL", pos=(4,0,0), rot=(0,50,0), boxes=[B((-6,-1,-26),(12,2,26),tan2)]),
            P("prop0", pos=(-15,0,0), boxes=[B((0,-6,-1),(1,12,2),(25,25,28)), B((-1,-1,-1),(2,2,2),(140,140,145))])]

def interceptor():
    dk = (60, 64, 70)
    body = [B((-18,-2,-2),(32,4,4),(230,232,235)), B((14,-2,-2),(4,4,4),(200,60,40)), B((18,-1,-1),(3,2,2),(120,35,25)),
            B((-18,-3,-3),(8,6,6),(70,70,75)), B((0,-3,-3),(2,6,6),(30,30,120)),
            B((-18,-6,-1),(7,12,1),dk), B((-18,0,-6),(7,1,12),dk),
            B((8,-4,-1),(3,8,1),dk), B((8,0,-4),(3,1,8),dk)]
    return [P("body", boxes=body)]

def radar():
    steel, dark = (150,155,160), (110,114,120)
    boxes = [B((-2,0,-2),(4,10,4),steel), B((-4,0,-4),(8,2,8),dark),
             B((3,8,-14),(2,14,28),(205,210,214),"grid"), B((1,8,-2),(2,14,4),dark),
             B((1,14,-14),(2,2,28),dark), B((5,13,-2),(3,2,4),(60,64,70)),
             B((-8,8,-5),(6,6,10),(90,94,100)), B((-1,10,-1),(2,2,2),(200,40,40))]
    return [P("antenna", boxes=boxes)]

def aa_rack():
    olive = (84, 94, 70)
    base = [B((-6,0,-8),(12,2,16),(70,80,62)), B((-2,2,-11),(4,6,2),(60,68,54)), B((-2,2,9),(4,6,2),(60,68,54))]
    rack = [B((-1,-1,-11),(2,2,22),(50,50,55)), B((-4,-3,-8),(2,6,16),(200,180,40))]
    for z0 in (-8, -4, 0, 4):
        rack += [B((-10,-2,z0),(24,4,4),olive), B((14,-2,z0),(1,4,4),(25,25,25)), B((-11,-2,z0),(1,4,4),(120,60,40))]
    return [P("base", boxes=base), P("rack", pos=(0,7,0), rot=(0,0,50), boxes=rack)]

def geran_drone():
    # барражирующий боеприпас: дельта-крыло, толкающий винт сзади, оливковый камуфляж
    olive, olive2, dk = (92, 98, 66), (78, 84, 56), (35, 36, 34)
    body = [B((-15,-3,-3),(34,6,6),olive), B((19,-2,-2),(4,4,4),(70,60,40)),
            B((-19,-2,-2),(4,4,4),dk), B((22,-1,-1),(2,2,2),(25,25,25)),
            B((10,-4,-4),(3,8,8),(60,110,180)), B((-4,3,-2),(6,2,4),olive2),
            B((-22,-1,-1),(3,2,2),(180,40,30))]
    wing = [B((-2,-1,-30),(10,2,27),olive2), B((-2,-1,3),(10,2,27),olive2),
            B((-1,1,29),(4,4,1),(60,60,65)), B((-1,1,-30),(4,4,1),(60,60,65))]
    return [P("body", boxes=body), P("wing", boxes=wing),
            P("finR", pos=(-18,2,0), rot=(0,0,20), boxes=[B((-2,0,-1),(6,10,1),olive)]),
            P("finL", pos=(-18,2,0), rot=(0,0,-20), boxes=[B((-2,0,-1),(6,10,1),olive)]),
            P("prop0", pos=(-21,0,0), boxes=[B((0,-7,-1),(1,14,2),(20,20,22)), B((-1,-1,-1),(2,2,2),(130,130,135))])]

MODELS = [("fpv_drone", fpv_drone()), ("recon_uav", recon_uav()), ("strike_uav", strike_uav()),
          ("geran_drone", geran_drone()),
          ("interceptor", interceptor()), ("radar", radar()), ("aa_rack", aa_rack())]
TEXFILE = {"fpv_drone": "fpv_drone", "recon_uav": "recon_uav", "strike_uav": "strike_uav",
           "geran_drone": "geran_drone",
           "interceptor": "interceptor", "radar": "radar", "aa_rack": "aa_rack"}

# ---------- упаковка UV ----------
def try_pack(sizes, W, H):
    order = sorted(range(len(sizes)), key=lambda i: -sizes[i][1])
    x = y = rowh = 0
    pos = [None] * len(sizes)
    for i in order:
        w, h = sizes[i]
        if w > W: return None
        if x + w > W: x = 0; y += rowh; rowh = 0
        if y + h > H: return None
        pos[i] = (x, y); x += w; rowh = max(rowh, h)
    return pos

def pack(sizes):
    for W, H in [(64,64),(128,64),(128,128),(256,128),(256,256)]:
        r = try_pack(sizes, W, H)
        if r: return r, W, H
    raise SystemExit("не влезло в текстуру")

def clampc(v): return max(0, min(255, int(v)))

def png(path, px):
    h = len(px); w = len(px[0])
    raw = b''.join(b'\x00' + b''.join(bytes(c) for c in row) for row in px)
    def ch(t, d):
        c = struct.pack('>I', len(d)) + t + d
        return c + struct.pack('>I', zlib.crc32(t + d) & 0xffffffff)
    open(path, 'wb').write(b'\x89PNG\r\n\x1a\n' + ch(b'IHDR', struct.pack('>IIBBBBB', w, h, 8, 6, 0, 0, 0))
                           + ch(b'IDAT', zlib.compress(raw)) + ch(b'IEND', b''))

def paint(img, x0, y0, w, h, color, pat, rng):
    for yy in range(y0, y0 + h):
        for xx in range(x0, x0 + w):
            n = rng.randint(-5, 5)
            col = tuple(clampc(c + n) for c in color)
            if w >= 3 and h >= 3 and (xx in (x0, x0 + w - 1) or yy in (y0, y0 + h - 1)):
                col = tuple(clampc(c * 0.78) for c in col)
            elif pat == "grid" and ((xx - x0) % 3 == 0 or (yy - y0) % 3 == 0):
                col = tuple(clampc(c * 0.82) for c in col)
            img[yy][xx] = col + (255,)

def fl(v): return "%.1fF" % float(v)
def rad(deg): return "0.0F" if deg == 0 else "(float) Math.toRadians(%.1f)" % deg

def const(name): return name.upper()
def camel(name):
    p = name.split("_"); return p[0] + "".join(x.capitalize() for x in p[1:])

java = ["package com.frontline.client;", "",
        "import com.frontline.Frontline;",
        "import net.minecraft.client.model.geom.ModelLayerLocation;",
        "import net.minecraft.client.model.geom.PartPose;",
        "import net.minecraft.client.model.geom.builders.CubeListBuilder;",
        "import net.minecraft.client.model.geom.builders.LayerDefinition;",
        "import net.minecraft.client.model.geom.builders.MeshDefinition;",
        "import net.minecraft.client.model.geom.builders.PartDefinition;",
        "import net.minecraft.resources.ResourceLocation;", "",
        "/** СГЕНЕРИРОВАНО скриптом gen_models.py: 3D-модели дронов, БПЛА, перехватчика, радара и ПВО. */",
        "public final class Models {",
        "    private Models() {}", "",
        "    private static ModelLayerLocation layer(String name) {",
        "        return new ModelLayerLocation(new ResourceLocation(Frontline.MODID, name), \"main\");",
        "    }", ""]

for name, parts in MODELS:
    allboxes = [(pi, b) for pi, p in enumerate(parts) for b in p["boxes"]]
    sizes = []
    for pi, b in allboxes:
        dx, dy, dz = b["d"]
        sizes.append((2 * (dz + dx), dz + dy))
    pos, W, H = pack(sizes)
    img = [[(0, 0, 0, 0)] * W for _ in range(H)]
    rng = random.Random(hash(name) & 0xffff)
    uv = {}
    for idx, (pi, b) in enumerate(allboxes):
        u, v = pos[idx]
        dx, dy, dz = b["d"]
        # грани развёртки: top, bottom, west, north, east, south
        faces = [(u + dz, v, dx, dz), (u + dz + dx, v, dx, dz), (u, v + dz, dz, dy),
                 (u + dz, v + dz, dx, dy), (u + dz + dx, v + dz, dz, dy), (u + 2 * dz + dx, v + dz, dx, dy)]
        for (fx, fy, fw, fh) in faces:
            paint(img, fx, fy, fw, fh, b["c"], b["pat"], rng)
        uv[id(b)] = (u, v)
    png(TEXDIR + TEXFILE[name] + ".png", img)

    C = const(name)
    spin_y = sorted(p["name"] for p in parts if p["name"].startswith("rotor"))
    spin_x = sorted(p["name"] for p in parts if p["name"].startswith("prop"))
    java.append('    public static final ModelLayerLocation %s_LAYER = layer("%s");' % (C, name))
    java.append("    public static final String[] %s_SPIN_Y = {%s};" % (C, ", ".join('"%s"' % n for n in spin_y)))
    java.append("    public static final String[] %s_SPIN_X = {%s};" % (C, ", ".join('"%s"' % n for n in spin_x)))
    java.append("")
    java.append("    public static LayerDefinition %sLayer() {" % camel(name))
    java.append("        MeshDefinition mesh = new MeshDefinition();")
    java.append("        PartDefinition root = mesh.getRoot();")
    for p in parts:
        java.append('        root.addOrReplaceChild("%s", CubeListBuilder.create()' % p["name"])
        for b in p["boxes"]:
            x, y, z = b["o"]; dx, dy, dz = b["d"]
            u, v = uv[id(b)]
            # перевод из «мировой» системы в систему модели Minecraft (x, y инвертированы)
            java.append("                .texOffs(%d, %d).addBox(%s, %s, %s, %s, %s, %s)" %
                        (u, v, fl(-(x + dx)), fl(-(y + dy)), fl(z), fl(dx), fl(dy), fl(dz)))
        px, py, pz = p["pos"]; rx, ry, rz = p["rot"]
        java.append("                , PartPose.offsetAndRotation(%s, %s, %s, %s, %s, %s));" %
                    (fl(-px), fl(-py), fl(pz), rad(-rx), rad(-ry), rad(rz)))
    java.append("        return LayerDefinition.create(mesh, %d, %d);" % (W, H))
    java.append("    }")
    java.append("")
    print(name, "текстура", W, "x", H, "боксов", len(allboxes))

java.append("}")
open(JAVA, "w", encoding="utf-8").write("\n".join(java) + "\n")
