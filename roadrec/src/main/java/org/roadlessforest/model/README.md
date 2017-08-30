Image labelling for training
============================

labelled_image table contains tile identifiers vs labels.

* These are only valid on the congo clip image
* Therefore need to find a more stable tiling scheme
* But, fine for now

Sequence file: 
* easy, Hadoop compatible
* Can write int arrays

Image preprocessor
* Key point is that prediction and training obviously need the same preprocessing
* Visual training doesn't matter, as long as the original data can be used

So...

Tile to sequence file??




