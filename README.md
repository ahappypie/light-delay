# light-delay
[![Docker Repository on Quay](https://quay.io/repository/ahappypie/light-delay/status "Docker Repository on Quay")](https://quay.io/repository/ahappypie/light-delay)

This server implements the astronomical calculations necessary to determine the distances (and therefore light-times) between two objects in this solar system.
In it's most basic operation, it takes a Unix timestamp (in milliseconds) and returns the light-time (also in milliseconds).

The calculations are based on methodology from Emory University's Department of Physics (available [here](http://www.physics.emory.edu/astronomy/events/mars/calc.html) 
and archived in this repository as [emory.html.gz](/emory.html.gz)) and data from VSOP87 as recorded in [libnova](http://libnova.sourceforge.net). 
VSOP87 datasets were converted from C to Scala and are embedded in this repository under the [vsop87](/src/main/scala-2.12/io/github/ahappypie/LightDelay/vsop87) package.

These calculations assume light can travel in a straight line, namely, there is not another body in the way. 
In the example below, Earth and Mars are close to opposition, which means the Sun could be in the direct path or near enough to the direct path to cause significant issues with communication.
This orbital positioning is called [Solar Conjunction](https://mars.nasa.gov/news/8506/whats-mars-solar-conjunction-and-why-does-it-matter/).
Therefore, the results returned by this service should only be used to determine approximate linear distance between bodies in light-time.

### Operation

#### Via Docker

```bash
docker run -p 50051:50051 --name light-delay quay.io/ahappypie/light-delay:0.0.1 
```

#### Via Source
You must set an environment variable for the GRPC server. Typically, I set the following variable:
```
GRPC_PORT=50051
```
If you do not set a port, it will default to 50051.

#### Endpoints
There is one service and two methods as defined in the protofile:

```LightDelay.GetSingleDelay``` -  requires parameter ```timestamp```, the Unix timestamp (in milliseconds) you would like to know the light delay for. 
Returns light delay in milliseconds between the specified orbital bodies. 

Optionally add parameters ```origin``` and  ```dest```, one of ```MERCURY | VENUS | EARTH | MARS | JUPITER | SATURN | URANUS | NEPTUNE```, and the delay will be for the specified origin/destination combination. 

Origin defaults to ```EARTH``` if none is given.

Destination defaults to ```MARS``` if none is given.

Origin and destination must be in all uppercase (or use the orbital position, where Mercury = 1).

I use [BloomRPC](https://github.com/uw-labs/bloomrpc) to quickly test against the protofile. This is a sample JSON input and expected response:
```json
{
  "timestamp": 1569609169337,
  "dest": "MARS"
}
```
returns
```json
{
  "delay": 1320096
}
```

This can be read as the following:
At 2019-09-27 1832 UTC, the light delay from Earth to Mars is 1320096 milliseconds, or about 22 minutes.

```LightDelay.GetAllDelay``` -  requires parameter ```timestamp```, the Unix timestamp (in milliseconds) you would like to know the light delay for.
Returns light delay in milliseconds between the specified orbital body and all other bodies in the dataset.

So,
```json
{
  "timestamp": 1569609169337,
  "origin": 3
}
```
returns
```json
{
  "entries": [
    {
      "body": "MERCURY",
      "delay": 652394
    },
    {
      "body": "VENUS",
      "delay": 834150
    },
    {
      "body": "MARS",
      "delay": 1320096
    },
    {
      "body": "JUPITER",
      "delay": 2724138
    },
    {
      "body": "SATURN",
      "delay": 4903029
    },
    {
      "body": "URANUS",
      "delay": 9465673
    },
    {
      "body": "NEPTUNE",
      "delay": 14459211
    }
  ]
}
```
### Dev
Ensure `git` and `sbt` are available in your shell.

Clone the repo:
```bash
git clone https://github.com/ahappypie/light-delay.git
```
Then compile the sbt project:
```bash
cd light-delay
sbt compile
```
To run, use the default sbt command:
```bash
sbt run
```