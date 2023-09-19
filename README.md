# FourierSeriesArt
Draw images out of Fourier decompositions of rotating circles stacked on one another!
<div align="center">Input</div>

![Input](https://i.imgur.com/9bS6QcO.png)
<div align="center">Output</div>

[![Output2](https://i.imgur.com/x7nCa4o.gif)](https://www.youtube.com/watch?v=F2OKzzynvP8)

# Purpose
The past few weeks my YouTube recommendations have been flooded with '#SoME3' videos, a competition and friendly event held intended to push creators to post interesting and eye-opening math videos. I continuously came across two areas of math which I have previously been exposed to, but hadn't done much of a *deep* dive of interest yielding complete comprehension yet. They were: Fourier/Laplace Transforms and Complex Numbers. While watching one video on the Fourier Series, they included a visual of the decomposition of a square wave into constituent frequencies by drawing circles independently rotating and then superimposed.

![Fourier Series](https://upload.wikimedia.org/wikipedia/commons/thumb/1/1a/Fourier_series_square_wave_circles_animation.gif/224px-Fourier_series_square_wave_circles_animation.gif)

That amazed me. Immediately I thought 'But what about input signals which are more... creative?' Not only that, but also 'What if we did this independently for an x and y axis, and merged them to get a recreated drawing of any input image?' I simply looked the idea up out of curiosity to see if anything has been posted on it, and unfortunately I am not the first human to have this idea. I AM, however, one of very shockingly few who have had the idea, pursued to implement it into software, and posted about it online. I found four other people who have uniquely done this. One is arguably the largest mathematics YouTube channel in existence (3Blue1Brown), another is an equally A tier YouTube channel which does coding projects (The Coding Train), the third is just a random person's *amazing* educational blog post (Olgaritme), and the fourth is a separate person's equally *amazing* educational blog post (Jezzamon). There are a few many more posted online, but it is abundantly obvious who just stole the code from The Coding Train and posted it as their own without actually understanding the math or making it from scratch. An important note is I refused to look into how any of these other people performed the feat. I wanted to go in purely with the math I have learned from my random SoME3 YouTube videos, and figure out how to implement it myself as a means to better my knowledge of both Fourier Series and Complex Numbers. So yeah, that is the project. 'Can I draw a picture by reverse engineering the size, rotation, and frequency of spinning circles which will be stacked on one another and just allowed to go wild?' (Spoiler, yes... yes I can).

# Configurable Variables
- Output Resolution [px] (Resolution of live simulations, rendered MP4, and rendered PNG)
- Unique Contour/Line Count (Input processes are processed into closed-loop contours, sometimes more than one. This is how many unique contours are drawn. Inclusions are in size-of-line order)
- Cycle Duration [s] (This is the length of time it takes for one full cycle in sims & renders. Renders run for twice this time because the first loop around has no tail, while the second has the full tail)
- Range of Circles (Fourier Series iteration count. 1 handles -1/0/1, 2 handles -2/-1/0/1/2, n handles -n...n. Don't make too large or instability is introduced bc of impossible percision overshoots)
- Simulation Time Step [s] (This is the time step between iterations of simluation for live previews and renders.
- Trail Lifetime % of Cycle [0 < x <= 1] (This is the % of a full cycle's time that a pixel will stay lit for. For example, .5 means only half the trail will be visible while the rest will have decayed into invisibility)
- Overlap Visibility [0 <= x <= 1] (This is the opacity of the trail which is drawn over top of the original image when downloading the "overlap png")

# Tutorial
1. Download [opencv_java480.dll](https://github.com/aaronskeelsofficial/FourierSeriesArt/raw/main/opencv_java480.dll) from this repo and ensure it's in your downloads folder (win+r, %USERPROFILE%\Downloads).
2. Run [FourierSeriesArt.jar](https://github.com/aaronskeelsofficial/FourierSeriesArt/raw/main/FourierSeriesArt.jar). Running jars is done via double click. You need Java installed.
3. From here you can go wild and do as you please, however I would advise you use the first "Preview Contour Calculation" button and configure the variables to see if you can get an ideal **target** output image. Some images are not processed by the contour extracter well and the output will never look acceptable. Some images may require some pre-processing to nudge the contour detection towards an ideal solution.
