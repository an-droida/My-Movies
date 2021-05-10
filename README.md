# My-Movies

### Used technologies and 3rd party libraries:
* retrofit2
* Gson
* room
* LiveData
* databinding
* viewbinding
* 3rd party API - https://api.themoviedb.org/3/


### The application consists of three activies: 
* SplashScreenActivity
* MoviesActivity 
* MovieDetailsActivity

### Tasks and Solutions
#### 1. Filter movies by popular and top rated: 
  movies lists are represented by one recycleriew with one adapter and one MutableList,
  movie filters are represented in the toolbar options menu, 
  by clicking on the menu items, relevant api is calling, observable methods are listening to
  the response and then movieItems list is updating.
* the movieItems pages is represented by pagination in recyclerview adapter.
  
#### 2. Details screen:
MovieDetailsActivity gets adapterPosition from MoviesActivity.
Using adapterPosition, current movie details is taken from movieItems, 
which is represented in companion object (to get access from another activity).

#### 3. Connection Handling:
  The user will be notified by snackBar if the connection is lost. 
  
  Connection handling is provided by two class: 
* Broadcast receiver - checka if the interntet is turned on;
* InetAddress - checks if device is connected to internet;


#### 4. Favorites
* Add, delete and check favorites method is represented in local (sqlite) database by room.
On the movie details page, when the user adds the movie to favorites, it will insert to the local database,
which can be viewed without internet on the MoviesActivity page.
* Also the movieItems list should know about the favorite movie, therefore each movie 
item has isFavorite variable and the check (if the movie is already in local database or not) 
is happening in recyclerview adapter.







