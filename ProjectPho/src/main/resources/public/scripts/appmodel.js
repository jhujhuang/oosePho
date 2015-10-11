var appModel = (function () {

    var state = {
        gameId : null,
        playerId : null,
        pieceType : null,
        gameState : null,
    };

    /* Create a new game. Note that this returns a deffered object that the control can chain on */
    var privateNewGame = function (type) {
      return $.post('/hareandhounds/api/games', JSON.stringify({pieceType : type}), null, 'json')
        .done(function (data) {
             state.gameId = data.gameId;
             state.playerId = data.playerId;
             state.pieceType = data.pieceType;
             console.log("That worked: Game Id: " + data.gameId + " Player Id: " + data.playerId) ;
         }).fail(function (jqXHR) {
             console.log('Error ' + jqXHR.status);
         });
    };

    /* Create a new game. Returns a deffered object that the control can chain on */
    var privateJoinGame = function (gameId) {
      return $.ajax({
         url : '/hareandhounds/api/games/' + gameId,
         method : "PUT",
         dataType : 'json'
      })
        .done(function (data) {
             state.gameId = data.gameId;
             state.playerId = data.playerId;
             state.pieceType = data.pieceType;
             console.log("We joined: Game Id: " + data.gameId + " Player Id: " + data.playerId) ;
         }).fail(function (jqXHR) {
             console.log('Error ' + jqXHR.status + ' ' + jqXHR.responseText);
         })
    };

    /* Fetch the game state from the server. Returns a deffered object that the control can chain on */
    var privateFetchState = function() {
        return $.get('/hareandhounds/api/games/' + state.gameId + '/state', "", null, 'json')
            .done(function (data) {
                state.gameState = data.state;
            }).fail(function (jqXHR) {
                console.log('Error ' + jqXHR.status);
            });
    };

    /* Fetch the board state from the server. Returns a deffered object that the control can chain on */
    var privateFetchBoard = function() {
        return $.get('/hareandhounds/api/games/' + state.gameId + '/board', "", null, 'json')
            .fail(function (jqXHR) {
               console.log('Error ' + jqXHR.status);
            });
    };

    /* Move a piece. Returns a deffered object that the control can chain on */
    var privateMovePiece = function(from, to) {
        var postData = {
            gameId : state.gameId,
            playerId : state.playerId,
            fromX : from.x,
            fromY : from.y,
            toX : to.x,
            toY : to.y
        };

        return $.post('/hareandhounds/api/games/' + state.gameId + "/turns", JSON.stringify(postData))
            .fail(function (jqXHR) {
                //Todo
                console.log('Error ' + jqXHR.status);
            });
    };

    //The object
    return {
        getGameState: function() { return state.gameState; },
        getGameId: function() { return state.gameId; },
        getPlayerId: function() { return state.playerId; },
        getPieceType: function() { return state.pieceType; },
        init: function() { },
        newGame: privateNewGame,
        joinGame: privateJoinGame,
        fetchState: privateFetchState,
        fetchBoard: privateFetchBoard,
        movePiece: privateMovePiece
    };

})();
