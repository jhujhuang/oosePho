var app = (function () {

    /* Fetch the board and update the view */
    var privateFetchAndUpdateBoard = function () {
        appModel.fetchBoard()
            .done(function (data) {
                boardView.updateBoard(data);
            }).fail(function (jqXHR) {
                appView.updateServerResponses('fetching the board data', jqXHR.status, jqXHR.statusText, null);
            });
    };

    /* Fetch the state information and update the view */
    var privateFetchAndUpdateState = function () {
        appModel.fetchState()
            .done(function (data) {
                appView.updateState(appModel.getGameId(), appModel.getPieceType(), data.state);
            }).fail(function (jqXHR) {
                appView.updateServerResponses('fetching the game state', jqXHR.status, jqXHR.statusText, null);
            });
    };

    //Convenience function to fetch and update both board and state
    var privateUpdateBoardAndState = function () {
        privateFetchAndUpdateBoard();
        privateFetchAndUpdateState();
    };

    /* Start a new game */
    var privateNewGame = function (type) {
        appModel.newGame(type)
            .done(function (data) {
                privateUpdateBoardAndState();
                window.setInterval(privateUpdateBoardAndState, 2000);
                appView.clearServerResponses();
            }).fail(function (jqXHR) {
                appView.updateServerResponses('starting a new game', jqXHR.status, jqXHR.statusText, null);
            })
    }

    /* Join a new game */
    var privateJoinGame = function (gameId) {
        appModel.joinGame(gameId)
            .done(function (data) {
                privateUpdateBoardAndState();
                window.setInterval(privateUpdateBoardAndState, 2000);
                appView.clearServerResponses();
           }).fail(function (jqXHR) {
               appView.updateServerResponses('joining a game', jqXHR.status, jqXHR.statusText, null);
           })
    };

    /* Move a piece */
    var privateMovePiece = function(from, to) {
        appModel.movePiece(from, to)
            .done(function (data) {
                privateUpdateBoardAndState();
                appView.clearServerResponses();
            }).fail(function (jqXHR) {
                appView.updateServerResponses('making a move', jqXHR.status, jqXHR.statusText, jqXHR.responseText);
            });
    };

    //The object
    return {
        newGame: privateNewGame,
        joinGame: privateJoinGame,
        movePiece: privateMovePiece,
        init: function() { },
        updateBoardAndState: privateUpdateBoardAndState
    };

})();
