App.User = Backbone.Model.extend({
    urlRoot:"http://localhost:9000/users"
})
App.UserCollection = Backbone.Collection.extend
    ({
        model: User,
        url: "http://localhost:9000/users"
    });