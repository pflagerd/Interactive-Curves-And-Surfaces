##How to install MSYS2

Go to their website here: http//www.msys2.org

Look for a button like this and click on it:

![Installing_MSYS21](Installing_MSYS2_images\Installing_MSYS21.png)

Choose all defaults, and finish by pressing Install:

![Installing_MSYS22](Installing_MSYS2_images\Installing_MSYS22.png)

When that's done you see:

![Installing_MSYS23](Installing_MSYS2_images\Installing_MSYS23.png)

Run MSYS2. It should look like this:

![Installing_MSYS24](Installing_MSYS2_images\Installing_MSYS24.png)

Enter pacman -Syu. You see something like this:

![Installing_MSYS25](Installing_MSYS2_images\Installing_MSYS25.png)

Press Enter to proceed with installation:

![Installing_MSYS26](Installing_MSYS2_images\Installing_MSYS26.png)

Close the window as the instructions say, then open a new one MSYS:

![Installing_MSYS27](Installing_MSYS2_images\Installing_MSYS27.png)

and type pacman -Su. You see something like this:

![Installing_MSYS28](Installing_MSYS2_images\Installing_MSYS28.png)

Type Enter to proceed with this installation too. Something like 36 packages will install.

Type pacman -S gcc, and install whatever comes up:

![Installing_MSYS29](Installing_MSYS2_images\Installing_MSYS29.png)

In the same way, get gcc-fortran:

![Installing_MSYS210](Installing_MSYS2_images\Installing_MSYS210.png)

Get the python compiler

![Installing_MSYS211](Installing_MSYS2_images\Installing_MSYS211.png)

I think you've got the hang of it now.

Get openssl, openssh, vim, git, svn, p7zip, cmake, make, autoconf, automake, libtool (gets tar)

