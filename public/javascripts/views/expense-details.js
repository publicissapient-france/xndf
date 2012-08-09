define([
    'zepto',
    'Underscore',
    'Backbone',
    'models/expense',
    'text!../../tpl/expense-detail.html'
], function ($, _, Backbone, Expense, template) {
    var ExpenseDetailsView = Backbone.View.extend({
        events:{
            "click #put":"saveExpense",
            "click #add":"addLine",
            "click #home":"close",
            "change":"change"
        },

        change:function(event){
            // Apply the change to the model
            var target = event.target;
            var path = target.name.split('.');
            var setValue=function(object,key,path,value){
                var nextKey=path.shift();
                if(nextKey){
                    setValue(object[key],nextKey, path, value);
                } else {
                    object[key]=value;
                }
            }
            var value=target.value;
            if(target.type=="number"){
                value=(+target.value);
            }
            setValue(this.model.attributes, path.shift(),path,value);
        },


        initialize:function () {
            this.slot=this.options.slot;
            this.model.on("reset", this.render, this);
            this.model.on("sync", this.render, this);
            this.model.on("change", this.render, this);
        },
        renderTemplate:function (json) {
            var $expenseElement = $(_.template(template, json));
            _.each(json.lines, function (line, index) {
                $expenseElement.find('[name*="expenseType"]')[index].value=line.expenseType;
            });
            return $expenseElement;
        },

        render:function () {
            this.$el.html(this.renderTemplate(this.model.toJSON()));
            this.slot.html(this.el);
            return this;
        },

        saveExpense:function () {
            this.model.save();
            return false;
        },
        addLine:function () {
            var defaultExpense = new Expense();
            this.model.set({lines: this.model.get('lines').concat(defaultExpense.get('lines'))}) ;
        },
        close:function () {
            $(this.el).unbind();
            $(this.el).empty();
        }

    });
    return ExpenseDetailsView;
});
