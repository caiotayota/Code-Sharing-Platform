# Code Sharing Platform
### <a href="https://hyperskill.org/projects/130" target="_blank">(a JetBrains Academy Project)</a>

https://user-images.githubusercontent.com/66436771/146869737-805771de-0893-42c2-91f9-3f7c963128e6.mp4

### Objectives

#### Code snippets should be accessible via UUID links.
#### `POST /api/code/new` should return a UUID of the snippet instead of a number.
#### `POST /api/code/new` should take a JSON object with a field code and two other fields:
1. `time` field contains the time (in seconds) during which the snippet is accessible.
2. `views` field contains a number of views allowed for this snippet.

#### 0 and negative values should correspond to the absence of the restriction.
#### `GET /code/new` should contain two elements on top of the others:
1. `<input id="time_restriction" type="text"/>` should contain the time restriction.
2. `<input id="views_restriction" type="text"/>` should contain the views restriction.

#### `POST` request should contain numbers, not strings.
#### `GET /api/code/latest` and `GET /code/latest` should not return any restricted snippets.
#### `GET /api/code/UUID` should not be accessible if one of the restrictions is triggered. Return 404 Not Found in this case and all the cases when no snippet with such a UUID was found.
#### `GET /api/code/UUID` should show what restrictions apply to the code piece. Use the keys time and views for that. A zero value (0) should correspond to the absence of the restriction.
1. `time` field contains the time (in seconds) during which the snippet is accessible.
2. `views` field shows how many additional views are allowed for this snippet (excluding the current one).
    
##### `GET /code/UUID` should contain the following elements:
1. `<span id="time_restriction"> ... </span>` in case the time restriction is applied.
2. `<span id="views_restriction"> ... </span>` in case the views restriction is applied.
