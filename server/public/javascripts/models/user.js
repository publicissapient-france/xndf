// Filename: models/project
define([
    'Underscore',
    'Backbone'
], function(_, Backbone){
    var userModel = Backbone.Model.extend({
        url:"http://localhost:9000/users"
    });
    // You usually don't return a model instantiated
    return userModel;
});