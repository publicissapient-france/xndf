define([
    'jQuery',
    'Underscore',
    'Backbone',
    'models/user',
    'text!../../tpl/user-list-item.html'
], function($, _, Backbone, user, userItemTemplate){
    var userItemView = Backbone.View.extend({
        tagName:"div", // Not required since 'div' is the default if no el or tagName specified

        render:function (eventName) {
            var el=$(this.el);
            el.html(_.template( userItemTemplate,this.model.toJSON()));
            return this;
        }
    });
    // Our module now returns an instantiated view
    // Sometimes you might return an un-instantiated view e.g. return projectListView
    return userItemView;
});
