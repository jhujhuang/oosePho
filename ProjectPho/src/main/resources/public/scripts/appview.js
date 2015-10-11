var appView = (function () {

    /* Update the game status on the corresponding panel */
    var privateUpdateState = function (gameId, pieceType, gameState) {
        switch(gameState) {
            case "WAITING_FOR_SECOND_PLAYER":
                var url = "http://localhost:8080/#/join/" + gameId;
                var html = "Waiting for a second player. Send this <a target=\"_blank\" href=\"" + url + "\">link</a> to a friend! Or open a new page yourself";
                $('#state').html(html);
                break;
            case "TURN_HOUND":
                var basic = "You are playing the " + (pieceType === "HOUND" ? 'Hound. ' : 'Hare. ')
                var turnAdvice = pieceType === "HOUND" ? "It is your turn!" : "It is your opponent's turn!";
                $('#state').text(basic + turnAdvice);
                break;
            case "TURN_HARE":
                var basic = "You are playing the " + (pieceType === "HOUND" ? 'Hound. ' : 'Hare. ')
                var turnAdvice = pieceType === "HARE" ? "It is your turn!" : "It is your opponent's turn!";
                $('#state').text(basic + turnAdvice);
                break;
            case "WIN_HARE_BY_ESCAPE":
                var status = pieceType === "HARE" ?
                    "Game Over: You played the hare and won by escaping!" :
                    "Game Over: You played the hound and lost! The hare has squeezed past you!";
                $('#state').html(status);
                break;
            case "WIN_HARE_BY_STALLING":
                var status = pieceType === "HARE" ?
                    "Game Over: You played the hare and won because the hounds are stalling!" :
                    "Game Over: You played the hound and lost by stalling!";
                $('#state').text(status);
                break;
            case "WIN_HOUND":
                var status = pieceType === "HOUND" ?
                    "Game Over: You played the hound and won by trapping the hare!" :
                    "Game Over: You played the hare and lost by getting trapped!";
                $('#state').text(status);
                break;
            default:
                $('#state').text("The game state is: " + gameState);
        };
        $('#state').parent().removeClass("invisible");
    };

    /* Update the server (request/response) status on the corresponding panel */
    var privateUpdateServerResponses = function(context, errorCode, statusText, responseData) {
        $('#serverStatus').parent().removeClass('invisible');
        $('#serverStatus').parent().addClass('panel-warning');

        $('#serverStatusHeading').text('' + errorCode + ' - ' + statusText);

        if (errorCode === 400) {
            var blurb = "The server returned an error when " + context +
                        ". This usually indicates that malformed data or a JSON parse error of some form. " +
                        "Maybe the web console has some information";
            $('#serverStatus').text(blurb);
            return;
        }

        if (errorCode === 500) {
            var blurb = "The server returned an error when " + context +
                        ". This usually indicates a bug or misbehavior on your code's part. " +
                        "Time to give the debugger a whirl";
            $('#serverStatus').text(blurb);
            return;
        }

        if (errorCode === 404) {
            var blurb = "The server returned an error when " + context +
                        ". This generally means an invalid game id or player id in our case. " +
                        "Did you restart the server and forget a refresh?";
            if (responseData !== null)  {
                $('#serverStatus').text(blurb + " Some more data: " + responseData);
            } else {
                $('#serverStatus').text(blurb);
            }
            return;
        }

        if (errorCode === 422) {
            var blurb = "The server returned status code 422 when " + context +
                        ". You either played an illegal move or played out of turn. " +
                        "Some more data: " + responseData;
            $('#serverStatus').text(blurb);
            return;
        }

        if (errorCode === 410) {
            var blurb = "The server returned status code 410 when " + context +
                        ". Looks like a second player has already joined the game.";
            $('#serverStatus').text(blurb);
            return;
        }

        var blurb = "The server returned status code " + errorCode + " when " + context ;
        $('#serverStatus').text(blurb);
    };

    var privateClearServerResponses = function () {
        $('#serverStatus').text("");
        $('#serverStatus').parent().addClass('invisible');
    }

    //The object
    return {
        init: function() { },
        updateState: privateUpdateState,
        updateServerResponses: privateUpdateServerResponses,
        clearServerResponses: privateClearServerResponses
    };

})();
