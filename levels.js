/**
 * Created by Cchiv on 10/03/2017.
 */
var plan = [{
    "map" :
    [   "                       ",
        "           o           ",
        "           x           ",
        "           x           ",
        "           x           ",
        "           x           ",
        "           x           ",
        "           x           ",
        "                       ",
        "                       ",
        "                       "],
    "hero" : [7, 11, "north"],
    "colors" : [],
    "tables" : [{"max" : 2, "name" : "F0"}],
    "commands" : ["^", ">", "<", "0"],
    "index" : 0
}, {
    "map" :
    [   "                       ",
        "            go         ",
        "           gv          ",
        "          gv           ",
        "         gv            ",
        "        gv             ",
        "       gv              ",
        "       x               ",
        "                       "],
    "hero" : [7, 7, "north"],
    "colors" : ["rgb(39, 174, 96)", "rgb(142, 68, 173)", "rgb(192, 57, 43)"],
    "tables" : [{"max" : 4, "name" : "F0"}],
    "commands" : ["^", ">", "<", "0"],
    "index" : 1
}, {
    "map" :
    [   "                       ",
        "           vxo         ",
        "           x           ",
        "         vxr           ",
        "         x             ",
        "       vxr             ",
        "       x               ",
        "     xxr               ",
        "                       "],
    "hero" : [7, 5, "east"],
    "colors" : ["rgb(39, 174, 96)", "rgb(142, 68, 173)", "rgb(192, 57, 43)"],
    "tables" : [{"max" : 4, "name" : "F0"}],
    "commands" : ["^", ">", "<", "0"],
    "index" : 2
}, {
    "map" :
        [   "                ",
            "    rxxxxxxr    ",
            "    x      x    ",
            "    x oxr  x    ",
            "    x   x  x    ",
            "    rxxxr  x    ",
            "           x    ",
            "  xxxxxxxxxr    ",
            "                "],
    "hero" : [7, 2, "east"],
    "colors" : ["rgb(39, 174, 96)", "rgb(142, 68, 173)", "rgb(192, 57, 43)"],
    "tables" : [{"max" : 3, "name" : "F0"}],
    "commands" : ["^", ">", "<", "0"],
    "index" : 3
}, {
    "map" :
        [   "                ",
            "    v  v  v     ",
            "    x  x  x     ",
            "    x  x  x     ",
            "    x  x  x     ",
            "    x  x  x     ",
            "  xxgxxgxxgxxo  ",
            "                "],
    "hero" : [6, 2, "east"],
    "colors" : ["rgb(39, 174, 96)", "rgb(142, 68, 173)", "rgb(192, 57, 43)"],
    "tables" : [{"max" : 5, "name" : "F0"}],
    "commands" : ["^", ">", "<", "0"],
    "index" : 4
}, {
    "map" :
        [   "                ",
            " xg         go  ",
            "  x         x   ",
            "  vxg     gxr   ",
            "    x     x     ",
            "    vxg gxr     ",
            "      x x       ",
            "      vxr       ",
            "                "],
    "hero" : [1, 1, "east"],
    "colors" : ["rgb(39, 174, 96)", "rgb(142, 68, 173)", "rgb(192, 57, 43)"],
    "tables" : [{"max" : 5, "name" : "F0"}],
    "commands" : ["^", ">", "<", "0"],
    "index" : 5
}, {
    "map" :
        [   "                ",
            "       v        ",
            "       x        ",
            "       x        ",
            "   vxxxgxxxo    ",
            "       x        ",
            "       x        ",
            "       x        ",
            "                "],
    "hero" : [7, 7, "north"],
    "colors" : ["rgb(39, 174, 96)", "rgb(142, 68, 173)", "rgb(192, 57, 43)"],
    "tables" : [{"max" : 4, "name" : "F0"}, {"max" : 2, "name" : "F1"}],
    "commands" : ["^", ">", "<", "0", "1"],
    "index" : 6
}];

if(typeof module != "undefined" && module.exports) {
    module.exports = plan;
}