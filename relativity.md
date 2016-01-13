Implementing a system to show an observer effects of relativity needs, different from classical physics:

-Awknoledgement of the finite speed of light (set as a constant, accepting c=1, and distances in light-seconds).
-Changes to photons hitting the observer in frequency (doppler shift), intensity (name???), and incoming angle (Lorentz transformation).
-Changes to the photon emitters (using the time they are observed in the past light cone).
-Changes to how objects move and accelerate in special relativity (lorentz tranformation), including its effect on time (proper time and time dialation)
([GR](https://en.wikipedia.org/wiki/General_relativity#Consequences_of_Einstein.27s_theory))-Changes to the path, frequency, and path length (minor?) of photons (primarily black-holes & dense objects).
(GR)-Time dilation in a gravitational potential (primarily black-holes & dense objects).
(GR)-Changes to how objects move in general relativity (primarily black-holes & dense objects.  May require weird coordinates...)
(GR)-Changes to how objects move near large spinning objects (frame dragging only, as orbital decay and precession effects sound boring)

Showing special relativity in a consistent manner needs:

-To be able to ask for an object to be rendered at some time in the past (light rays take time to travel)
-To be able to "rotate coordinates" between space and time: Lorentz transformations (https://en.wikipedia.org/wiki/Lorentz_transformation)
-To be able to independently track time on different actors (and possibly the universe itself), since their clocks will not necessarily synchronize (unless they are moving very slowly).

Showing general relativity needs:

...I don't actually know at the moment.  Might be able to cheat it with pseudoaccelerations, pseudorotations, and lensing effects?

Some definitions, so we are all clear on these:

[Spacetime](https://en.wikipedia.org/wiki/Spacetime): 4th dimensional system with 3D space and time.  Not really anything out of the ordinary.
Proper time: time measured by a clock following a path through spacetime.  If you walk around with a watch, it keeps proper time.
[Spacetime interval](https://en.wikipedia.org/wiki/Spacetime#Spacetime_intervals_in_flat_space): s^2, where s^2 = dr^2 - c^2 * dt^2.  Kinda like the square of the distance between two points, but we throw in time*c (a distance!), and we subtract it instead of add.  Okay, not the most intuitive but it has some nice properties: it is invariant (more on that later) and its sign (or status as 0) divides the interval into 3 categories:
s^2<0: time-like (this is a path that could be a point travelling through time).  Proper time intervals can be calculated by sqrt(-s^2)... which looks weird, but don't worry about it.
s^2>0: space-like (this is a path that could follow the length of a ruler in a given time, but couldn't follow one end of a ruler through time)
s^2=0: light-like (only something at the speed of light could follow this path) (or maybe we subtracted the same point from itself)
Invariant: a property that does not change through a lorentz transformation.
[Lorentz Transformation](https://en.wikipedia.org/wiki/Lorentz_transformation).  Imagine the stuff you do to rotate between x & y (it involves sine and cosine).  This is between time and space with an input arg of speed.  It is also hyperbolic (rather than spherical).  It also doesn't involve any trig functions (unless you prescribe to using rapidity).
[Rapidity](https://en.wikipedia.org/wiki/Rapidity): The "angle" for a lorentz transformation, except it is given to cosh & sinh, and 2pi of it doesn't mean anything.  A function of v, and close to v/c for low values of v.  Has the interesting property that for 1D travel (x,ct) it is additive when you add speeds via a lorentz transformation.
