#### The parametric form

Hopefully, you've got something that looks like a circle now:

![1560840305010](artifacts/1581021252243.png)

The new form of the circle

$$x = \cos\theta$$ 	$$y = \sin\theta$$

where $0 \le \theta < 2\pi$

is called a ***parametric form*** of the circle.

The two variables $x$ and $y$ are replaced by formulas given in terms of a third variable called $\theta$. This third variable, $\theta$ is called ***a parameter***.

So in a way, we've exchanged a new independent variable $\theta$ for the old independent variable: $x$

That simplifies our programming task quite a bit, but this particular ***parametrization*** still has a pretty high computational cost.

Transcendental functions, such as $\cos$ and $\sin$ are usually implemented on computers using a Maclauren series like this (for example):

​	$$\cos \theta = \sum\limits_{i=0}^{\infty}(-1)^i \frac{{\theta}^{2i}}{(2i)!}\tag{Lesson004.1}$$

You can see there's a whole lot of multiplying, dividing, adding and subtracting involved in such an implementation.

Wouldn't it be nice if we could come up with a very simple parametrization that involved much simpler formulae?

There is another set of trigonometric formulae which we can use:

$$ x= \cos \theta=\frac{(1 -\tan^2 \theta)}{(1+\tan^2\theta)}\tag{Lesson004.2}$$

$$y = \sin \theta=\frac{2\tan \theta}{(1+\tan^2\theta)} \tag{Lesson004.3}$$

If we define a new parameter $t = \tan\theta$, and substitute $t$ for $\tan\theta$ in $(Lesson004.2)$ and $(Lesson004.3)$, we end up with this very nice, simple pair of equations:

$$x=\frac{1-t^2}{1+t^2}\tag{Lesson004.4}$$

$$y = \frac{2t}{1+t^2}\tag{Lesson004.5}$$

Where $-\infty < t < \infty$. 

You can see that to compute $x$ requires only two multiplications, one addition and one division. That's considerably fewer operations than is required for one single term of the Maclauren series for $$\cos \theta$$ shown in Lesson004.1.

How many operations is required to compute $y$?

Now, what about the problematic $-\infty < t < \infty$?

ASSIGNMENT: Your assignment is to write OpenGL code to draw a circle using these much simpler parametric formulae while *minimizing* the number of computations.

In other words, find a way to overcome the need for $\pm \infty$.

