##What is relativity?
The [special theory of relativity](https://en.wikipedia.org/wiki/Special_relativity) can be summed up as the can of worms opened by the following two statements:
* The laws of physics stay the same regardless of what inertial frame of reference is being looked at
* The speed of light is a constant (in a vacuum, for all observers, regardless of reference frame)

The [general theory of relativity](https://en.wikipedia.org/wiki/General_relativity) can be summed up as the the additional barrel of somethings opened by the following statements:
* Acceleration from gravity or from a sufficiently smooth normal type of acceleration cannot be distinguished apart from each other (paraphrasing a bit)
* Light doesn't figure it out either

See, it isn't so bad, right?  What could be simpler?

##What is Different with Relativity?
Implementing a system to show an observer effects of relativity needs, different from classical physics:
* Acknowledgement of the finite speed of light (set as a constant, accepting c=1, and distances in light-seconds).
* Changes to photons hitting the observer in frequency ([doppler shift](https://en.wikipedia.org/wiki/Relativistic_Doppler_effect)), [intensity](https://en.wikipedia.org/wiki/Relativistic_Doppler_effect#Doppler_effect_on_intensity), and incoming angle ([Lorentz transformation](https://en.wikipedia.org/wiki/Lorentz_transformation)).
* Changes to the photon emitters position (using the time they are observed in the past light cone) and shape ([length contraction](https://en.wikipedia.org/wiki/Length_contraction), probably best with Lorentz transformation of vertices).
* Changes to how objects move and accelerate in special relativity (lorentz tranformation), including its effect on time (proper time and time dilation)
* ([GR](https://en.wikipedia.org/wiki/General_relativity#Consequences_of_Einstein.27s_theory))-Changes to the [path](https://en.wikipedia.org/wiki/Gravitational_lens#Explanation_in_terms_of_space.E2.80.93time_curvature), frequency, and path length (minor?) of photons (primarily black-holes & dense objects).
* (GR)-Time dilation in a gravitational potential (primarily black-holes & dense objects).
* (GR)-Changes to how objects move in general relativity (primarily black-holes & dense objects.  [May require weird coordinates...](https://en.wikipedia.org/wiki/Schwarzschild_metric#The_Schwarzschild_metric))
* (GR)-Changes to how objects move near large spinning objects (frame dragging only, as orbital decay and precession effects sound boring)

##How can this be done in a program trying to render stuff?
Showing special relativity in a consistent manner needs (with the "universe is stationary approach"):
* (Physics) Objects' flow of time to be controlled by the observer-object distance (light rays take time to travel), how it changes (relative motion), and any additional time dilation (on both ends, depending on how the object's actions are defined ("universal" or proper time)).
* (Physics?) Shift object apparent locations and shapes based on photons accelerating to the observer's frame of reference
* (Physics) The tools to do these calculations:
  * (Physics) To be able to "rotate coordinates" between space and time: [Lorentz transformations](https://en.wikipedia.org/wiki/Lorentz_transformation)
  * (Physics) [To add velocities](https://en.wikipedia.org/wiki/Velocity-addition_formula)
* Independently track time on different objects (and possibly the universe itself), since their clocks will not necessarily synchronize (unless they are moving very slowly).
* Try not to have things act too weird going really fast and/or being away from the origin (
* (Graphics) Doppler shift all the things (color, brightness?).  Color temperature or a redshift parameter could be used.
  * At excessive speeds, the ~4 Kelvin CMB would eventually become visible.

Showing general relativity needs:
* ...I don't actually know at the moment.  [Pretty sure the fully correct way is way past the scope we are willing to deal with.](https://en.wikipedia.org/wiki/Numerical_relativity#Early_results) Might be able to cheat it with pseudoaccelerations (observers shouldn't be able to tell, right?), pseudorotations, and [lensing effects](https://en.wikipedia.org/wiki/Gravitational_lens#Explanation_in_terms_of_space.E2.80.93time_curvature)?
  * Raytracing would brute force solve photon paths, but does not immediately seem like a good idea...
  * Black holes will have to dispose of objects they eat.  Preferably without breaking the simulation.

##Definitions
Some definitions, so we are all clear on these:
* [Spacetime](https://en.wikipedia.org/wiki/Spacetime): 4th dimensional system with 3D space and time.  Not really anything out of the ordinary.
* Proper time: time measured by a clock following a path through spacetime.  If you walk around with a watch, it keeps proper time.
* "Universal" time: treating this as proper time of some default inertial frame of the universe.  In quotes due to not knowing a specific term for this.
* [Spacetime interval](https://en.wikipedia.org/wiki/Spacetime#Spacetime_intervals_in_flat_space): s^2, where s^2 = dr^2 - c^2 * dt^2.  Kinda like the square of the distance between two points, but we throw in time*c (a distance!), and we subtract it instead of add.  Okay, not the most intuitive but it has some nice properties: it is invariant (more on that later) and its sign (or status as 0) divides the interval into 3 categories:
  * s^2<0: time-like (this is a path that could be a point travelling through time).  Proper time intervals can be calculated by sqrt(-s^2)... which looks weird, but don't worry about it.
  * s^2>0: space-like (this is a path that could follow the length of a ruler in a given time, but couldn't follow one end of a ruler through time)
  * s^2=0: light-like (only something at the speed of light could follow this path) (or maybe we subtracted the same point from itself)
* Invariant: a property that does not change through a lorentz transformation.
* [Lorentz Transformation](https://en.wikipedia.org/wiki/Lorentz_transformation).  Imagine the stuff you do to rotate between x & y (it involves sine and cosine).  This is between time and space with an input arg of *velocity*.  It is also hyperbolic (rather than spherical).  It also doesn't involve any trig functions (unless you prescribe to using rapidity).  Also known as:
  * Boost (eg. boosting to a frame of reference)
  * Accelerating to a moving frame of reference
* [Rapidity](https://en.wikipedia.org/wiki/Rapidity): The "angle" for a lorentz transformation, except it is given to cosh & sinh, and 2pi of it doesn't mean anything.  A function of v, and close to v/c for low values of v.  Has the interesting property that for 1D travel (x,ct) it is additive when you add speeds via a lorentz transformation.

##Confessions
Okay, a lot of things are actually simpler.
