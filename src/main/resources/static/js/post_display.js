
//use a class rather than an ID to bind the click event
$('.thumb-img').click(function () {
    //gets the source of the thumbnail
    var source = $(this).attr('src');
    $("#banner").fadeOut(function () {
        //fades banner out and upon completion changes source image and fades in.
        $(this).attr("src", source);
        $(this).fadeIn();
    });
});