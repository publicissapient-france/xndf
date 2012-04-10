// Filename: collections/projects
define([
    'Underscore',
    'Backbone',
    // Pull in the Model module from above
    'models/user'
], function(_, Backbone, userModel){
    var userCollection = Backbone.Collection.extend({
        model: userModel,
        url:"http://localhost:9000/users"
    });
    // You don't usually return a collection instantiated
    return userCollection;
});