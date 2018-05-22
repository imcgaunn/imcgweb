# Building / Running Frontend
To launch a development server running on localhost:8080
running a less-optimized version of the frontend code,
run:

`shadow-cljs watch client`

shadow-cljs will kindly reload changes to the frontend code live.

# Building Backend
The backend workflow is still in progress because there
isn't much use for a backend yet. To start the build
for what is there, which is really just a placeholder,
run:

`shadow-cljs watch server`

# Running Backend
To run the (currently skeletal) backend, run:

`node target/main.js`