var boardView = (function () {

    /* Get the location of the spot as an x-y coordinate. You can get this from the specially named slot classes
     * associated with each spot
     */
    var privateGetSpotLocation = function (spot) {
        spot = $(spot);
        return _.reduce(
            spot.attr("class").split(" "),
            function (spotNumber, class_) {
                var matchResults = class_.match(/^spot-(\d+)-(\d+)$/);
                if (matchResults) {
                    return { x : matchResults[1], y : matchResults[2] };
                }
                else {
                    return spotNumber;
                }
            }
        );
    };

    /* Given a board JSON as defined by the web api, update the board */
    var privateUpdateBoard = function (board) {
        //TODO: Optimize this to use a single loop and no unnecessar clears

        //Clear the grid
        var possibleStates = ["empty", "hare", "hound"];
        $("#game .grid .spot").removeClass(possibleStates.join(" "));

        //Update with the current board state
        for(var i = 0; i < board.length; i++) {
            var piece = board[i];
            var state = piece.pieceType.toLowerCase();
            if (! _.contains(possibleStates, state)) {
                throw "State `" + state + "' not included in possible states: `" + possibleStates + "'.";
            }

            $(".spot-" + piece.x + "-" + piece.y).addClass(state);
        }
    };

    /* Show a particular location as selected */
    var privateSetSelection = function(oldSelectedLocation, newSelectedLocation) {
         $("#game .grid .spot").removeClass("selected");
         if (newSelectedLocation !== null) {
             $(".spot-" + newSelectedLocation.x + "-" + newSelectedLocation.y).addClass("selected");
         }
    };

    /* Handle all clicks on spots in the grid and trigger a custom higher level event */
    var privateClickEventTranslator = function(handler) {
        var bControlDown = false;
        var spots = $("#game .grid .spot");
        spots.on("mousedown", function (event) {
            var spot = $(this);
            var spotLocation = privateGetSpotLocation(spot) ;
            //$(event.data).trigger("spotClicked", [spotLocation]);
            event.preventDefault();
            handler(event, spotLocation);
        });
    };

    return {
        init: function() {
            //Initialize the selection manager
            selectionManager.init(this);

            //Set up the click events to be processed via the selection manager
            privateClickEventTranslator(selectionManager.selectionHandler);
        },
        updateBoard: privateUpdateBoard,
        setSelection: privateSetSelection
    };
})();
