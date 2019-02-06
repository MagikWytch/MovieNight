const CLIENT_ID = "456752195903-qnk1em5fckgkobmj4lf5ionvb8rc7te4.apps.googleusercontent.com";


function start() {
    gapi.load('auth2', function () {
        auth2 = gapi.auth2.init({
            client_id: CLIENT_ID,
            scope: "https://www.googleapis.com/auth/calendar.events"
        });
    });
}

$('#signinButton').click(function () {
// signInCallback defined in step 6.
    auth2.grantOfflineAccess().then(signInCallback);
});

function signInCallback(authResult) {
    console.log('authResult', authResult);
    if (authResult['code']) {

        // Hide the sign-in button now that the user is authorized, for example:
        $('#signinButton').attr('style', 'display: none');
        {

        }
        // Send the code to the server
        $.ajax({
            type: 'POST',
            url: 'http://localhost:8080/calendar/storeauthcode',
            // Always include an `X-Requested-With` header in every AJAX request,
            // to protect against CSRF attacks.
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            },
            contentType: 'application/octet-stream; charset=utf-8',
            success: function (result) {
            },
            processData: false,
            data: authResult['code']
        });
    } else {
        // There was an error.
    }
}

$('#search-for-movie-button').on('click', () => {
    let key = $('#movie-search-value').val();
    searchForMovies(key);
});

function searchForMovies(key) {

    $.ajax({
        type: 'GET',
        url: 'http://localhost:8080/movie/search/' + key,
        // Always include an `X-Requested-With` header in every AJAX request,
        // to protect against CSRF attacks.
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        },
        contentType: 'application/octet-stream; charset=utf-8',
        success: function (result) {
            if (result.length > 0) {
                addMoviesToList(result);
            }
            else {
                $('#movie-box').empty();
                $('#movie-box').append('<p>Movie could not be found!</p>');
            }
        },
        error: function (result) {
            console.log("error" + result.httpRequestStatusCode)
        },
        processData: false,
    });
}

function addMoviesToList(result) {
    $('#movie-box').empty();

    let movieList = $('<ul></ul>');
    $('#movie-box').append(movieList);

    for (let movie of result) {
        let newMovie = createNewMovieItem(movie);
        $(movieList).append(newMovie);

    }
}

function createNewMovieItem(movie) {

    let movieItem = $(`
        <li>
            <a href="#">
                ${movie.Title} ${movie.Year}
            </a>
        </li>`);

    movieItem.on('click', () => {
        let id = movie.imdbID;
        getOneMovie(id);
    });

    return movieItem;

}

function getOneMovie(id) {
    $.ajax({
        type: 'GET',
        url: 'http://localhost:8080/movie/id/' + id,
        // Always include an `X-Requested-With` header in every AJAX request,
        // to protect against CSRF attacks.
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        },
        contentType: 'application/octet-stream; charset=utf-8',
        success: function (result) {
            displayOneMovie(result);
        },
        error: function (result) {
            console.log("error" + result.httpRequestStatusCode)
        },
        processData: false,
    });

}

function displayOneMovie(movie) {
    $('#movie-box').empty();

    let newMovieView = createNewMovieDisplayView(movie);
    $('#movie-box').append(newMovieView);
}

function createNewMovieDisplayView(movie) {

    let movieView = $(`
        
        <img src="${movie.Poster}">
        <h3>${movie.Title} ${movie.Year}</h3>
        <p>${movie.Runtime}</p>
        <p>${movie.Plot}</p>
        <button type="button" class='select-movie'>Select movie</button>
             
          `);


    $(document).on('click', '.select-movie', () => {
        let movieTitle = movie.Title;
        let movieRuntime = movie.Runtime;

        $('#movie-box').empty();
        $('#movie-box').append(`<p>${movie.Title} ${movie.Year} has been selected</p>`);
        getFreeTimes();
    });

    return movieView;
}

function getFreeTimes() {
    $.ajax({
        type: 'GET',
        url: 'http://localhost:8080/calendar/getFreeTime/',
        // Always include an `X-Requested-With` header in every AJAX request,
        // to protect against CSRF attacks.
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        },
        contentType: 'application/octet-stream; charset=utf-8',
        success: function (result) {
            addFreeTimesToList(result);
        },
        error: function (result) {
           // console.log("error" + result.httpRequestStatusCode)
        },
        processData: false,
    });
}

function addFreeTimesToList(result) {
    console.log("Inside add free times");
    result.forEach(time => $('#free-time-box').append(`<li><a href="#">${time}</a></li>`));
}

