/**
 * Un Mediator, pour controller la communication entre objets
 * @type {Function}
 *
 * Ex d'usage : Mediator.subscribe('UserConnected', HeaderModalService, HeaderModalService.RefreshHeader);
 *              ConnectionService.ConnectSucces = function(){
 *                  Mediator.publish('UserConnected', this);
 *              };
 *
 */
var mediator = (function () {
    var channels = {};

    var subscribe = function (channel, context, func) {
        if (!mediator.channels[channel]) {
            mediator.channels[channel] = []
        }
        console.log("Subscribing to channel " + channel + " : " + func.name);
        mediator.channels[channel].push({
            context: context,
            func: func
        });
    };

    var publish = function (channel) {
        if (!this.channels[channel]) {
            console.log("No such channel : " + channel);
            return false
        }

        var args = Array.prototype.slice.call(arguments, 1);

        for (var i = 0; i < mediator.channels[channel].length; i++) {
            var sub = mediator.channels[channel][i];
            sub.func.apply(sub.context, args)
        }
    };
    return {
        channels: {},
        subscribe: subscribe,
        publish: publish
    };
}());