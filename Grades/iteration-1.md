# Iteration 1 Evaluation - Group 8

**Evaluator: Scott Smith (mailto:scott@cs.jhu.edu)**

## Positive Points:

* Your idea is original
* You have thought through a lot of the challenging issues including synchronization when multiple users are editing, and client vs server image processing

## Things to Improve / Evolve:

### Use-cases

* The Collaborative editing use-case is too big, it is really many use-cases: take control to edit, pencil draw, apply filter, ..., finish edit.

*(-2 points)*

### GUI / use-case alignment

* A few of the use-cases, e.g. versioning, have no GUI.  This is only a minor issue at this stage but try to fill it out for iteration 2 as it will help you understand your app better.

*(-1 point)*

### Collaborative editing

* In discussing this you had thought about how to take turns, which is good.  Make sure to refine this in your iteration 2.  For example when one user is finished, probably the GUI should show "available for edit" rather than going on to the next person.


### Architecture

* Keep working on this for iteration 2, try to fix what javascript libraries you want in terms of what will help make your client coding the easiest, and get image processing libraries and how they will hook into your server refined.  You should have made a proposal for what server to use along with what database.  You probably want to use Postgres if SQLite is not working adequately, it is replacing MySQL these days.

*(-1 point)*


## Overall:

Overall your project is going very well.  You do have some big challenges in terms of the integration of server-side filters, synchronization, and the javascript coding challenges so make sure to get your proposed architecture refined quickly so you can get to coding.

**Grade: 96/100**
