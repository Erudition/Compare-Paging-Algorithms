Paging Algorithms Comparison
=========================
Skip to the [Java Files](https://github.com/Erudition/Compare-Paging-Algorithms/tree/master/OSproject/src), or read the project's  [documentation wiki](https://github.com/Erudition/Compare-Paging-Algorithms/wiki).

This was a final project for a college course on kernels ("Operating Systems", COMS221) with the purpose of camparing the efficiency of paging algorithms. After seeing the attempts by other group members I decided the most flexible and powerful (and therefore best, since speed didn't matter here) way to implement this was to follow Object-Oriented philosophy. I started by making a 'Page' class and making methods to manipulate Pages. In that one night I had eventually rewritten the entire project from scratch:

A 'Page' is a class with a contentBit, referenceBit, touchCount and any other properties each algorithm may need. Therefore a process is an array of pages - in fact, all the processes together (the 'hardDrive') is a 2D array of pages. From the Page[][] array, elements are added to a Java Collection. I taught myself Collections for this part because although pages don't get reordered in real life RAM, we can do whatever's convenient programmatically because the result is the same.

The benefits of using a Collection (List) in my implementation are many - for First In First Out, pages coming in are simply add()ed to the Queue, and page-outs remove() a page and add() a new one. The removed page will naturally be the oldest since the List stays in order.

Extending this to other algorithms was then easy - Collections are sort()able. I simply re-sort the collection based on each Page's properties (for example, the most-touched pages at the bottom) and then continue to remove pages from the top. This means each algorithm is basically the same as FIFO with around 1 additional line of code.

You can see the pages sliding up and sorting by setting the "outputMode" variable from "normal" to "matrix". To evaluate results in excel, try the "spreadsheet" mode and pipe the output to a CSV file. Finally, the "teamGUI" mode (a bit complicated) produces output compatible with the GUI that my team members made for visualizing the page movement. (not included)

Thanks to this paradigm, checking if a page was already resident in the memory table was a simple as table.contains(page). Since objects are passed by reference, it always will match the correct instance from hardDrive[process][page].

Where's the flexibility? Well, this project was designed for a RAM capacity of 16 pages, to run (in Round-Robin) 10 processes of 10 pages each in two algorithms and compare them. However, these are all constants, and should not be hardcoded - right at the top of the program you'll see you change these values to simulate whatever values you want!

In short, the program has a 10% chance each cycle of the currently touched page being modified (marked by '*') and a 90% bias towards pages 8 and 9 of a process (as if they had high Locality). It is heavily commented so you should have no problem seeing what goes on.
