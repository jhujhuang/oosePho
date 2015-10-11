var selectionManager = (function () {

    //Current selection
    var selected = null;

    //Create a handler that can fire move and selection. Does move with Alt+click
    var privateCreateSelectionHandlerWithAlt = function(obj, event, spotLocation) {
        var bModifierDown = event.altKey;

        if (bModifierDown) {
            if (selected !== null) {
                $(obj).trigger("move", [selected, spotLocation]);
                $(obj).trigger("selectionChange", [selected, null]);
                selected = null;
            }
        } else {
            if (selected !== null && selected.x === spotLocation.x && selected.y === spotLocation.y) {
                $(obj).trigger("selectionChange", [selected, null]);
                selected = null;
            } else {
                $(obj).trigger("selectionChange", [selected, spotLocation]);
                selected = spotLocation;
            }
        }
    };

    //Create a handler that can fire move and selection
    var privateCreateSelectionHandler = function(obj, event, spotLocation) {
        if (selected !== null && selected.x === spotLocation.x && selected.y === spotLocation.y) {
            $(obj).trigger("selectionChange", [selected, null]);
            selected = null;
        } else if (selected === null) {
            $(obj).trigger("selectionChange", [selected, spotLocation]);
            selected = spotLocation;
        } else {
            $(obj).trigger("move", [selected, spotLocation]);
            $(obj).trigger("selectionChange", [selected, null]);
            selected = null;
        }
    };

    //Choose the selection handler delegate
    var selectionHandlerDelegate = privateCreateSelectionHandlerWithAlt ;

    //A single point from which to dispatch to the actual selection handler
    var privateCreateSelectionHandlerDispatcher = function(obj) {
        var handler = function(event, spotLocation) {
            selectionHandlerDelegate(obj, event, spotLocation);
        }
        return handler;
    };

    //The object
    return {
        init: function(obj) {
            this.selectionHandler = privateCreateSelectionHandlerDispatcher(obj);
        },
        useAltClick: function (val) {
            if (val) {
                selectionHandlerDelegate = privateCreateSelectionHandlerWithAlt;
            } else {
                selectionHandlerDelegate = privateCreateSelectionHandler;
            }
        },
        selectionHandler: null
    };

})();
