# light-delay
This server implements the astronomical calculations necessary to determine the distances (and therefore light-times) between two objects in this solar system.
In it's most basic operation, it takes a Unix timestamp (in milliseconds) and returns the light-time (also in milliseconds).

You must set an environment variable for the GRPC server. Typically, I set the following variable:
```
GRPC_PORT=50051
```
If you do not set a port, it will default to 50051.

#### Endpoints
There is one service and method as defined in the protofile:

```LightDelay.GetLightDelay``` -  requires query parameter ```ts```, the unix timestamp (in milliseconds) you would like to know the light delay for. 
Returns light delay in milliseconds. 
Optionally add query parameter ```dest```, one of ```MARS | JUPITER | SATURN```, and the delay will be for the specified destination. Defaults to ```MARS``` if none is given.

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
#### Dev
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