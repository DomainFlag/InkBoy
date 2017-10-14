/**
 * Created by Cchiv on 17/03/2017.
 */
var scale = 55;
var speed = 150;
var currentSelected = null;

var speedPackage = [150, 50];

function Vector(x, y) {
    this.x = x;
    this.y = y;
}

Vector.prototype.plus = function(other) {
    return new Vector(this.x + other.x, this.y + other.y);
};

Vector.prototype.times = function(lambda) {
    return new Vector(this.x * lambda, this.y * lambda);
};

let directions = {
    "west"  : new Vector(-1, 0),
    "north" : new Vector(0,  -1),
    "east"  : new Vector(1,  0),
    "south" : new Vector(0,  1)
};

let directionsClasses = {
    "<" : "left",
    "^" : "up",
    ">" : "right"
};

let classesToString = {
    "left" : "west",
    "up" : "north",
    "right" : "east"
};

function Game(plan) {
    this.width = plan.map[0].length;
    this.height = plan.map.length;
    this.grid = [];
    this.actors = [];
    for(let g = 0; g < this.height; g++) {
        let newLine = [];
        for(let h = 0; h < this.width; h++) {
            let element = plan.map[g][h];
            let currentPos = new Vector(h, g);
            newLine.push(new legend[element](currentPos));
        }
        this.grid.push(newLine);
    }
    this.actors.push(new Player(new Vector(plan.hero[1], plan.hero[0]), plan.hero[2]));
    this.tables = plan.tables;
    this.commands = plan.commands;
    this.colors = plan.colors;
    this.index = plan.index;
    this.state = true;
}

function Player(pos, compass) {
    this.pos = pos;
    this.compass = compass;
}

Player.prototype.action = function(direc) {
    var cordDirect = directions[direc];
    this.pos.plus(cordDirect);
};

var legend = {
    "x" : Block,
    "g" : BlockGreen,
    "v" : BlockViolet,
    "r" : BlockRed,
    "o" : Target,
    " " : Blank
};

function Block(pos) {
    this.pos = pos;
    this.color = "rgb(41, 128, 185)";
}

function BlockGreen(pos) {
    this.pos = pos;
    this.color = "rgb(39, 174, 96)";
}

function BlockViolet(pos) {
    this.pos = pos;
    this.color = "rgb(142, 68, 173)";
}

function BlockRed(pos) {
    this.pos = pos;
    this.color = "rgb(192, 57, 43)";
}

function Target(pos) {
    this.pos = pos;
    this.color = "rgb(44, 62, 80)";
}

function Blank(pos) {
    this.pos = pos;
    this.color = "rgba(236, 240, 241, 0.3)";
}

function DOMCreator(type, className) {
    var DOMElement = document.createElement(type);
    if(className)
        DOMElement.className = className;
    return DOMElement;
}

function DOMDisplay(level) {
    var canvas = document.querySelector("canvas");
    canvas.width  = document.querySelector(".content_column").clientWidth;
    canvas.height = document.querySelector(".content_column").clientHeight;
    this.wrap = canvas;
    this.level = level;
    this.makeBackground();
    this.makeTools();
    this.makeUtilities();
}

DOMDisplay.prototype.makeBackground = function() {
    var cx = this.wrap.getContext("2d");
    /******************************************************************/
    /**
     * Clear the canvas when a command gets executed, so we redraw every time when actor moves on canvas.
     */
    let width = (this.wrap.width - (this.level.width * scale))/2;
    let height = (this.wrap.height - (this.level.height * scale))/2;
    cx.clearRect(0, 0, this.wrap.width, this.wrap.height);
    /******************************************************************/
    /**
     * We draw the actual game pad, with blocks and their corresponding fillColor, also add of border
     * to blocks that aren't Blank, add more feel to the game.
     */
    cx.beginPath();
    this.level.grid.forEach(function(line) {
        line.forEach(function(element) {
            let posBlock = element.pos.times(scale);
            posBlock =  posBlock.plus(new Vector(width, height));
            cx.fillStyle = element.color;
            cx.fillRect(posBlock.x, posBlock.y, scale, scale);
            if(Object.getPrototypeOf(element).constructor.name !== "Blank") {
                cx.lineWidth = 1;
                cx.strokeRect(posBlock.x-0.5, posBlock.y-0.5, scale, scale);
                cx.stroke();
            }
        }, 0)
    }, 0);
    /******************************************************************/
    /**
     * @param {posActor} the position of actor scaled for canvas
     * @param {compass} the direction of the actor (north, east, south, west),
     * and corresponding drawing, alternative image and use of mirror transcend
     * with rotate.
     */
    let posActor = this.level.actors[0].pos.times(scale);
    posActor = posActor.plus(new Vector(width, height));
    cx.fillStyle = "#f1c40f";
    let compass = this.level.actors[0].compass;
    if(compass === "north") {
        cx.moveTo(posActor.x+scale/6, posActor.y+5/6*scale);
        cx.lineTo(posActor.x+5/6*scale, posActor.y+5/6*scale);
        cx.lineTo(posActor.x+scale/2, posActor.y+scale/6);
    } else if(compass === "east") {
        cx.moveTo(posActor.x+scale/6, posActor.y+5/6*scale);
        cx.lineTo(posActor.x+5/6*scale, posActor.y+scale/2);
        cx.lineTo(posActor.x+scale/6, posActor.y+scale/6);
    } else if(compass === "south") {
        cx.moveTo(posActor.x+scale/6, posActor.y+scale/6);
        cx.lineTo(posActor.x+5/6*scale, posActor.y+scale/6);
        cx.lineTo(posActor.x+scale/2, posActor.y+5/6*scale);
    } else {
        cx.moveTo(posActor.x+scale/6, posActor.y+scale/2);
        cx.lineTo(posActor.x+5/6*scale, posActor.y+scale/6);
        cx.lineTo(posActor.x+5/6*scale, posActor.y+5/6*scale);
    }
    cx.closePath();
    cx.fill();
};

DOMDisplay.prototype.makeTools = function() {
    /******************************************************************/
    /**
     * Create the button with the event listener to run the input commands
     * @param {button} button
     */
    document.querySelector("#run").addEventListener("click", run.bind(this));
    /******************************************************************/
    /**
     * Create div containing 3 Glyphs, 2 to manipulate the speed of execution of the commands chosen and first and the last to
     * reverse speed and break the execution in case of loop;
     */

    document.querySelector(".play").addEventListener("click", function(e) {
        speed = speedPackage[0];
    });

    document.querySelector(".forward").addEventListener("click", function(e) {
        speed = speedPackage[1];
    });

    document.querySelector(".stop").addEventListener("click", function(e) {
        this.level.state = false;
        let time = setTimeout(function() {
            this.restart(this.level.index);
        }.bind(this), speed);
    }.bind(this));
};

DOMDisplay.prototype.makeUtilities = function() {
    let game_play = document.querySelector("#game_play");
    let game_action = document.querySelector("#game_action");
    /******************************************************************/
    /**
     * Create the table that contains the values to be parsed to execute the commands
     * @param {table} choiceTable
     */
    this.level.tables.forEach(function(directions, index) {
        let table = DOMCreator("div", "table_row");

        let caption = DOMCreator("div", "caption");
        caption.textContent = "F" + index;

        table.appendChild(caption);
        for(let g = 0; g < directions.max; g++) {
            let cell = DOMCreator("div", "cell");
            cell.addEventListener("click", removeCommand);
            table.appendChild(cell);
        }
        game_play.appendChild(table);
    });
    /******************************************************************/
    /**
     * Create table containing the commands at the disposal of user
     * @param {table} table
     */
    let table = DOMCreator("div", "table_row");
    for(let g = 0; g < this.level.commands.length; g++) {
        let cell = DOMCreator("div", "cell " + directionsClasses[this.level.commands[g]]);
        if(!isNaN(this.level.commands[g])) {
            cell.textContent = "F" + this.level.commands[g];
        }
        cell.addEventListener("click", appendCommand);
        table.appendChild(cell);
    }
    game_action.insertBefore(table, document.querySelector(".game_action_row"));
    /******************************************************************/
    /**
     * Create table that contains extra commands at the disposal of the user(colors, fill)
     * @param {table} table
     */
    if(this.level.colors.length !== 0) {
        let table = DOMCreator("div", "table_row");
        for(let g = 0; g < this.level.colors.length; g++) {
            let cell = DOMCreator("div", "cell");
            cell.addEventListener("click", appendColor);
            cell.style["background-color"] = this.level.colors[g];
            table.appendChild(cell);
        }
        game_action.insertBefore(table, document.querySelector(".game_action_row"));
    }

    let select = document.querySelector(".table_row").childNodes[1];
    select.style.border = "2px solid #F47D47";
    currentSelected = select;
};

DOMDisplay.prototype.restart = function(index) {
    this.level = new Game(plan[index]);
    this.makeBackground();
};

DOMDisplay.prototype.nextLevel = function(index) {
    document.querySelector("#level").textContent = "Level " + (index+1);
    this.level = new Game(plan[index+1]);
    this.makeBackground();
    let old_commands = document.querySelector("#game_play");
    while(old_commands.firstChild)
        old_commands.removeChild(old_commands.firstChild);
    let old_tools = document.querySelector("#game_action");
    while(old_tools.querySelector(".table_row")) {
        old_tools.removeChild(old_tools.querySelector(".table_row"));
    }
    this.makeUtilities();
};

function appendCommand(event) {
    if(event.target.textContent.length > 0) {
        currentSelected.className = "cell";
        currentSelected.textContent = event.target.textContent;
    } else {
        currentSelected.className = event.target.className;
        currentSelected.textContent = null;
    }
}

function appendColor(event) {
    currentSelected.style["background-color"] = event.target.style["background-color"];
}

function removeCommand(event) {
    event.target.className = "cell";
    event.target.style["background-color"] = null;
    event.target.textContent = null;
    currentSelected.style.border = "2px solid black";
    currentSelected = event.target;
    currentSelected.style.border = "2px solid #F47D47";
}

function queryConstructor(input) {
    let query = [];
    input.childNodes.forEach(function(table_row) {
        let row = [];
        let cells = table_row.childNodes;
        for(let i = 1; i < cells.length; i++) {
            let cell = cells[i];
            if(cell.className.split(" ")[1]) {
                row.push({
                    "direction": classesToString[cell.className.split(" ")[1]],
                    "color" : cell.style["background-color"] || null
                });
            } else {
                row.push({
                    "direction": Number(cell.textContent.slice(1)),
                    "color" : cell.style["background-color"] || null
                });
            }
        }
        query.push(row);
    });
    console.log(query);
    return query;
}

let redirectCompass = ["north", "east", "south", "west"];

function run(event) {
    let query = queryConstructor(document.querySelector("#game_play"));
    /*Reinitialize the state so at the stop button, the stack won't be stuck with infinite timeOuts iterations in case the user mistakenly created a loop*/
    this.level.state = true;
    /******************************************************************/
    /**
     * Refactor once again this shit code below:
     */
    let object = this;
    function updateLoop(indexLine, indexColumn) {
        let actorPos = object.level.actors[0].pos;
        let furtherActions = function(line, col) {
            /*If we reach the end of query array then stop the loop, if not next command*/
            if(col < query[line].length && object.level.state) {
                updateLoop(line, col);
            }
        };
        let loop = setTimeout(function() {
            let currentCommand =  query[indexLine][indexColumn];
            let currentBlock = object.level.grid[actorPos.y][actorPos.x];
            /**
             * @param {currentCommand.direction}
             * In case the next command it's a function call(number type), call the function and the next command in the current function waiting for
             * the first one to finish.
             * @param {currentCommand.color}
             * @param {currentBlock.color}
             * The commands are executed only when the block color where the actor is positioned corresponds to the command' color or the
             * command' color is null(universal works on every valid colored block.
             */
            if(!isNaN(currentCommand.direction) && (currentCommand.color === currentBlock.color || currentCommand.color === null)) {
                furtherActions(currentCommand.direction, 0);
                furtherActions(indexLine, indexColumn+1);
            } else if(currentBlock.color === currentCommand.color || currentCommand.color === null && currentCommand.direction) {
                /*If the user uses rotate left/right => change the actor's compass (input: <-, pos: down, result:->)
                 *Else the command is go straight, make necessary changes, if it arrives
                 *on blank block -> lose/restartLevel, if it arrives on target block -> win/nextLevel */
                if(currentCommand.direction === "west" || currentCommand.direction === "east") {
                    let currentInd = redirectCompass.indexOf(object.level.actors[0].compass);
                    if(currentCommand.direction === "west") {
                        if(currentInd === 0)
                            currentInd = 3;
                        else currentInd--;
                        object.level.actors[0].compass =  redirectCompass[currentInd];
                    } else object.level.actors[0].compass =  redirectCompass[(currentInd+1)%4];
                    furtherActions(indexLine, indexColumn+1);
                } else {
                    let nextActorPos = actorPos.plus(directions[object.level.actors[0].compass]);
                    let nextConstructorName = Object.getPrototypeOf(object.level.grid[nextActorPos.y][nextActorPos.x]).constructor.name;
                    if(nextConstructorName !== "Blank") {
                        let actorDirection = object.level.actors[0].compass;
                        object.level.actors[0].pos = object.level.actors[0].pos.plus(directions[actorDirection]);
                        if(Object.getPrototypeOf(object.level.grid[nextActorPos.y][nextActorPos.x]).constructor.name === "Target") {
                            object.nextLevel(object.level.index);
                        } else furtherActions(indexLine, indexColumn+1);
                    } else object.restart(object.level.index);
                }
            } else  {
                /*The current command to be executed, doesn't exist, just pass a blank option till it finds a valid one.*/
                furtherActions(indexLine, indexColumn+1);
            }
            /*Show the new background after the changes made by one command on canvas with a
            speed time interval to observe the change till the next one occurs*/
            object.makeBackground();
        }, speed);
    }
    updateLoop(0, 0);

}
