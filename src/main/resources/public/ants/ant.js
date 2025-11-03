var container = document.getElementById("container");
var tracker = document.getElementById("tracker");
var trackCount = 0;

ant1.style.left = '0px';
ant1.style.top = '0px';
ant2.style.left = '495px';
ant2.style.top = '0px';
ant3.style.left = '0px';
ant3.style.top = '495px';
ant4.style.left = '495px';
ant4.style.top = '495px';

function simulate() {
    clearPath();
    var ant1 = document.getElementById("ant1");
    var ant2 = document.getElementById("ant2");
    var ant3 = document.getElementById("ant3");
    var ant4 = document.getElementById("ant4");
    ant1.style.left = '0px';
    ant1.style.top = '0px';
    ant2.style.left = '495px';
    ant2.style.top = '0px';
    ant3.style.left = '0px';
    ant3.style.top = '495px';
    ant4.style.left = '495px';
    ant4.style.top = '495px';
    var trackCount = 0;
    tracker.innerHTML = trackCount;
    var id = setInterval(frame, 25);
    function frame() {
        if (antsTouched(ant1, ant3)) {
            clearInterval(id);
        } else {
            ant1Move = moveTowards(ant1, ant3);
            ant2Move = moveTowards(ant2, ant1);
            ant3Move = moveTowards(ant3, ant4);
            ant4Move = moveTowards(ant4, ant2);

            trackCount+=1;
            tracker.innerHTML = '<b>Distance Traveled (px):</b> ' + trackCount;
            move(ant1, ant1Move);
            move(ant2, ant2Move);
            move(ant3, ant3Move);
            move(ant4, ant4Move);
        }
    }
}

const pause = () => {
    alert("Paused! Press OK to resume.")
}

function antsTouched(antA, antB) {
    var deltaX = Math.abs(parseFloat(antB.style.left) - parseFloat(antA.style.left));
    var deltaY = Math.abs(parseFloat(antB.style.top) - parseFloat(antA.style.top));

    return deltaX < 1 && deltaY < 1;
}

function moveTowards(antA, antB) {
    var deltaX = parseFloat(antB.style.left) - parseFloat(antA.style.left);
    var deltaY = parseFloat(antB.style.top) - parseFloat(antA.style.top);

    var mag = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));

    return {
        moveX: (parseFloat(antA.style.left) + deltaX / mag),
        moveY: (parseFloat(antA.style.top) + deltaY / mag)
    };
}

function move(ant, move) {
    var line = document.createElement("div");
    line.className = "marker";
    line.style.left = ant.style.left;
    line.style.top = ant.style.top;
    container.appendChild(line);

    ant.style.left = move.moveX + 'px'
    ant.style.top = move.moveY + 'px'
}

function clearPath() {
    while (container.firstChild) {
        container.removeChild(container.firstChild);
    }
    var antdiv1 = document.createElement("div");
    var antdiv2 = document.createElement("div");
    var antdiv3 = document.createElement("div");
    var antdiv4 = document.createElement("div");
    antdiv1.className = "ant"
    antdiv2.className = "ant"
    antdiv3.className = "ant"
    antdiv4.className = "ant"
    antdiv1.id = "ant1"
    antdiv2.id = "ant2"
    antdiv3.id = "ant3"
    antdiv4.id = "ant4"
    container.appendChild(antdiv1)
    container.appendChild(antdiv2)
    container.appendChild(antdiv3)
    container.appendChild(antdiv4)
}
