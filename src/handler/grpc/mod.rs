use tonic::{Request, Response, Status};

use crate::pb;
use pb::delay::v1::light_delay_server::LightDelay;
use pb::delay::v1::{SingleRequest, SingleResponse, AllRequest, AllResponse};
use crate::pb::delay::v1::DelayEntry;

use crate::functions;

#[derive(Default)]
pub struct LightDelayImpl {}

#[tonic::async_trait]
impl LightDelay for LightDelayImpl {
    async fn get_single_delay(&self, request: Request<SingleRequest>, ) -> Result<Response<SingleResponse>, Status> {
        println!("Got a single delay request from {:?}", request.remote_addr());

        let req = request.into_inner();

        let reply = SingleResponse {
            delay: functions::delay(req.origin, req.dest, req.timestamp) as u32,
        };
        Ok(Response::new(reply))
    }

    async fn get_all_delay(&self, request: Request<AllRequest>) -> Result<Response<AllResponse>, Status> {
        println!("Got an all delay request from {:?}", request.remote_addr());

        let req = request.into_inner();

        let reply = AllResponse {
            entries: vec![
                DelayEntry{ body: 1, delay: functions::delay(req.origin, 1, req.timestamp) as u32 },
                DelayEntry{ body: 2, delay: functions::delay(req.origin, 2, req.timestamp) as u32 },
                DelayEntry{ body: 3, delay: functions::delay(req.origin, 3, req.timestamp) as u32 },
                DelayEntry{ body: 4, delay: functions::delay(req.origin, 4, req.timestamp) as u32 },
                DelayEntry{ body: 5, delay: functions::delay(req.origin, 5, req.timestamp) as u32 },
                DelayEntry{ body: 6, delay: functions::delay(req.origin, 6, req.timestamp) as u32 },
                DelayEntry{ body: 7, delay: functions::delay(req.origin, 7, req.timestamp) as u32 },
                DelayEntry{ body: 8, delay: functions::delay(req.origin, 8, req.timestamp) as u32 }
            ],
        };
        Ok(Response::new(reply))
    }
}