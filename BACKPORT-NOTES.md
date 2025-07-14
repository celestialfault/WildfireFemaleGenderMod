>[!warning]
> This branch is not going to see any support outside of fixing serious issues/crashes.
> (aka, "this was not all that fun so I've decided I'm not going to be backporting to a version this old ever again")

A few notes:

- Armor texture data has not at all been tested. It *might* explode. Hopefully not, though.
- This version is directly incompatible with clients/servers running the officially released 1.20.1 version.
- Armor texture data *(hopefully)* supports a `texture` property to change the armor's texture location, instead of using the default guess at where it might be.

Missing features:

- Support for `{"x": x, "y": y}` texture data in resource packs
- Holiday features
- Contributor capes
- Contributor names tooltip doesn't show when hovering over the text in the mod's GUI
