// Filename: libs/jquery/jquery.js

define([
// Load the original jQuery source file
    'order!//ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js'
], function(){
    // Tell Require.js that this module returns a reference to jQuery
    return $;
});