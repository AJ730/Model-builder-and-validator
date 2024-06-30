import { v4 as uuidv4 } from 'uuid';
import { protectedResources } from '../authConfig';
const { callApiWithToken } = require("../util/useFetch");

beforeAll(() => {
    require("whatwg-fetch")
})



test("Test apiUserProjectHolder", async () => {
      const expectedProjectHolder = {id : 0, clientId: 0}
  
      jest.spyOn(window, "fetch").mockImplementation(() => {
        const fetchResponse = {
          ok: true,
          json: () => Promise.resolve(expectedProjectHolder)
        };
        return Promise.resolve(fetchResponse);
      });

      const endpoint = protectedResources.apiUserProjectHolder.endpoint
      const token = uuidv4();
      const account = {
          idTokenClaims: { oid: uuidv4()}
      }
      const userId = 0

      const projectHolder = await callApiWithToken(token, endpoint, account, "json", "POST", 
                    JSON.stringify({ id: userId }), new Headers({ 'content-type': 'application/json' }))
  
      expect(projectHolder).toMatchObject(expectedProjectHolder);

      window.fetch.mockRestore();  
});

test("Test apiGetProjectHolder", async () => {
    const expectedProjectHolder = {id : 0, clientId: 0}

    jest.spyOn(window, "fetch").mockImplementation(() => {
      const fetchResponse = {
        ok: true,
        json: () => Promise.resolve(expectedProjectHolder)
      };
      return Promise.resolve(fetchResponse);
    });

    const endpoint = protectedResources.apiGetProjectHolder.endpoint
    const token = uuidv4();
    const account = {
        idTokenClaims: { oid: uuidv4()}
    }
    const projectHolderId = 0

    const projectHolder = await callApiWithToken(token, endpoint, account, "json", "POST", 
                  JSON.stringify({ id: projectHolderId }), new Headers({ 'content-type': 'application/json' }))

    expect(projectHolder).toMatchObject(expectedProjectHolder);

    window.fetch.mockRestore();  
});

test("Test apiGetProjectsFromHolderUser", async () => {
    const expectedProjects = [
        {id : 0, title: 'Project 1', description : 'Project 1', projectHolderId : 0, adminId : uuidv4()},
        {id : 1, title: 'Project 1', description : 'Project 1', projectHolderId : 0, adminId : uuidv4()}
    ];

    jest.spyOn(window, "fetch").mockImplementation(() => {
      const fetchResponse = {
        ok: true,
        json: () => Promise.resolve(expectedProjects)
      };
      return Promise.resolve(fetchResponse);
    });

    const endpoint = protectedResources.apiGetProjectsFromHolderUser.endpoint
    const token = uuidv4();
    const account = {
        idTokenClaims: { oid: uuidv4() }
    }
    const userId = 0

    const projectList = await callApiWithToken(token, endpoint, account, "json", "POST", 
                  JSON.stringify({ id: userId }), new Headers({ 'content-type': 'application/json' }))

    expect(projectList).toMatchObject(expectedProjects);

    window.fetch.mockRestore();  
});

test("Test apiGetUserWithEmail", async () => {
    const userId = uuidv4()
    const expectedUser = 
        {userId : userId, email : "hello@google.com", username : "Hello World", registrationDate : Date.now()}

    jest.spyOn(window, "fetch").mockImplementation(() => {
      const fetchResponse = {
        ok: true,
        json: () => Promise.resolve(expectedUser)
      };
      return Promise.resolve(fetchResponse);
    });

    const endpoint = protectedResources.apiGetUserWithEmail.endpoint
    const token = uuidv4();
    const account = {
        idTokenClaims: { oid: userId }
    }
    const userEmail = "hello@google.com"

    const user = await callApiWithToken(token, endpoint, account, "json", "POST", 
                  JSON.stringify({ email : userEmail }), new Headers({ 'content-type': 'application/json' }))

    expect(user).toMatchObject(expectedUser);

    window.fetch.mockRestore();  
});

test("Test apiUserInfo", async () => {
    const userId = uuidv4()
    const expectedUser = 
        {userId : userId, email : "hello@google.com", username : "Hello World", registrationDate : Date.now()}

    jest.spyOn(window, "fetch").mockImplementation(() => {
      const fetchResponse = {
        ok: true,
        json: () => Promise.resolve(expectedUser)
      };
      return Promise.resolve(fetchResponse);
    });

    const endpoint = protectedResources.apiUserInfo.endpoint
    const token = uuidv4();
    const account = {
        idTokenClaims: { oid: userId }
    }

    const user = await callApiWithToken(token, endpoint, account, "json", "POST",)

    expect(user).toMatchObject(expectedUser);

    window.fetch.mockRestore();  
});

test("Test apiGetUserWithId", async () => {
  const userId = uuidv4()
  const expectedUser = 
      {userId : userId, email : "hello@google.com", username : "Hello World", registrationDate : Date.now()}

  jest.spyOn(window, "fetch").mockImplementation(() => {
    const fetchResponse = {
      ok: true,
      json: () => Promise.resolve(expectedUser)
    };
    return Promise.resolve(fetchResponse);
  });

  const endpoint = protectedResources.apiGetUserWithId.endpoint
  const token = uuidv4();
  const account = {
      idTokenClaims: { oid: userId }
  }

  const user = await callApiWithToken(token, endpoint, account, "json", "POST", 
                JSON.stringify({ id : userId }), new Headers({ 'content-type': 'application/json' }))

  expect(user).toMatchObject(expectedUser);

  window.fetch.mockRestore();  
});

test("Test apiGetAllUsers", async () => {
  const userId1 = uuidv4()
  const userId2 = uuidv4()
  const expectedUsers = [
    {userId : userId1, email : "hello1@google.com", username : "Hello World 1", registrationDate : Date.now()},
    {userId : userId2, email : "hello2@google.com", username : "Hello World 2", registrationDate : Date.now()}
  ]
     

  jest.spyOn(window, "fetch").mockImplementation(() => {
    const fetchResponse = {
      ok: true,
      json: () => Promise.resolve(expectedUsers)
    };
    return Promise.resolve(fetchResponse);
  });

  const endpoint = protectedResources.apiGetAllUsers.endpoint
  const token = uuidv4();
  const account = {
      idTokenClaims: { oid: uuidv4() }
  }

  const users = await callApiWithToken(token, endpoint, account, "json", "POST")

  expect(users).toMatchObject(expectedUsers);

  window.fetch.mockRestore();  
});
 

test("Test apiDeleteUser", async () => {
  const userId = uuidv4()
  const expectedUser = 
      {userId : userId, email : "hello@google.com", username : "Hello World", registrationDate : Date.now()}

  jest.spyOn(window, "fetch").mockImplementation(() => {
    const fetchResponse = {
      ok: true,
      json: () => Promise.resolve(expectedUser)
    };
    return Promise.resolve(fetchResponse);
  });

  const endpoint = protectedResources.apiDeleteUser.endpoint
  const token = uuidv4();
  const account = {
      idTokenClaims: { oid: userId }
  }

  const user = await callApiWithToken(token, endpoint, account, "json", "POST", 
                JSON.stringify({ id : userId }), new Headers({ 'content-type': 'application/json' }))

  expect(user).toMatchObject(expectedUser);

  window.fetch.mockRestore();  
});

test("Test apiCreateContainer", async () => {
  const userId = uuidv4()

  jest.spyOn(window, "fetch").mockImplementation(() => {
    const fetchResponse = {
      ok: true,
      json: () => Promise.resolve(null)
    };
    return Promise.resolve(fetchResponse);
  });

  const endpoint = protectedResources.apiCreateContainer.endpoint
  const token = uuidv4();
  const account = {
      idTokenClaims: { oid: userId }
  }

  const formData = new FormData()
  formData.append("projectId", 0)
  formData.append("blobName", "Video 1")
  formData.append("csv", "Detection 1")
  formData.append("description", "This is Project 1")
  formData.append("classes", "Classes 1")
  formData.append("name", "Project 1")          

  await callApiWithToken(token, endpoint, account, "json", "POST", formData)

  window.fetch.mockRestore();  
});

test("apiDeleteContainer", async () => {
  const expectedContainer = {
      id: 0, blobId: 0, csvId: 0, projectId: 0, persistentCsvId: 0, submissionId: 0, framerate: 30,
      name: 'testName', description: 'testDesc', csvName: 'testCsv', blobName: 'testBlob', className: 'testClass'
  }

  jest.spyOn(window, "fetch").mockImplementation(() => {
      const fetchResponse = {
          ok: true,
          json: () => Promise.resolve(expectedContainer)
      };
      return Promise.resolve(fetchResponse);
  });

  const apiDeleteContainer = protectedResources.apiDeleteContainer.endpoint
  const token = uuidv4();
  const account = {
      idTokenClaims: { oid: uuidv4 }
  }
  const containerId = 0

  const container = await callApiWithToken(token, apiDeleteContainer, account, "json", "POST",
      JSON.stringify({ id: containerId }), new Headers({ 'content-type': 'application/json' }))

  expect(container).toMatchObject(expectedContainer);

  window.fetch.mockRestore();
});

test("apiGetContainer", async () => {
  const expectedContainer = {
      id: 0, blobId: 0, csvId: 0, projectId: 0, persistentCsvId: 0, submissionId: 0, framerate: 30,
      name: 'testName', description: 'testDesc', csvName: 'testCsv', blobName: 'testBlob', className: 'testClass'
  }

  jest.spyOn(window, "fetch").mockImplementation(() => {
      const fetchResponse = {
          ok: true,
          json: () => Promise.resolve(expectedContainer)
      };
      return Promise.resolve(fetchResponse);
  });

  const apiGetContainer = protectedResources.apiGetContainer.endpoint
  const token = uuidv4();
  const account = {
      idTokenClaims: { oid: uuidv4 }
  }
  const containerId = 0

  const container = await callApiWithToken(token, apiGetContainer, account, "json", "POST",
      JSON.stringify({ id: containerId }), new Headers({ 'content-type': 'application/json' }))

  expect(container).toMatchObject(expectedContainer);

  window.fetch.mockRestore();
});

test("apiGetProjects", async () => {
  const expectedProjects = [{ id: 0, title: 'testTitle', description: 'testDesc', projectHolderId: 0, adminId: 0 },
  { id: 1, title: 'testTitle', description: 'testDesc', projectHolderId: 1, adminId: 1 }
  ]
  jest.spyOn(window, "fetch").mockImplementation(() => {
      const fetchResponse = {
          ok: true,
          json: () => Promise.resolve(expectedProjects)
      };
      return Promise.resolve(fetchResponse);
  });

  const apiGetProjects = protectedResources.apiGetProjects.endpoint
  const token = uuidv4();
  const account = {
      idTokenClaims: { oid: uuidv4 }
  }

  const projects = await callApiWithToken(token, apiGetProjects, account, "json", "POST", new Headers({ 'content-type': 'application/json' }))

  expect(projects).toMatchObject(expectedProjects);

  window.fetch.mockRestore();
});

test("apiGetProject", async () => {
  const expectedProject = { id: 0, title: 'testTitle', description: 'testDesc', projectHolderId: 0, adminId: 0 }
  jest.spyOn(window, "fetch").mockImplementation(() => {
      const fetchResponse = {
          ok: true,
          json: () => Promise.resolve(expectedProject)
      };
      return Promise.resolve(fetchResponse);
  });

  const apiGetProject = protectedResources.apiGetProject.endpoint
  const token = uuidv4();
  const account = {
      idTokenClaims: { oid: uuidv4 }
  }
  const projectId = 0

  const project = await callApiWithToken(token, apiGetProject, account, "json", "POST",
      JSON.stringify({ id: projectId }), new Headers({ 'content-type': 'application/json' }))

  expect(project).toMatchObject(expectedProject);

  window.fetch.mockRestore();
});

test("apiProjectContainers", async () => {
  const expectedContainers = [{
      id: 0, blobId: 0, csvId: 0, projectId: 0, persistentCsvId: 0, submissionId: 0, framerate: 30,
      name: 'testName', description: 'testDesc', csvName: 'testCsv', blobName: 'testBlob', className: 'testClass'
  },
  {
      id: 1, blobId: 1, csvId: 1, projectId: 1, persistentCsvId: 1, submissionId: 1, framerate: 30,
      name: 'testName', description: 'testDesc', csvName: 'testCsv', blobName: 'testBlob', className: 'testClass'
  }]
  jest.spyOn(window, "fetch").mockImplementation(() => {
      const fetchResponse = {
          ok: true,
          json: () => Promise.resolve(expectedContainers)
      };
      return Promise.resolve(fetchResponse);
  });

  const apiProjectContainers = protectedResources.apiProjectContainers.endpoint
  const token = uuidv4();
  const account = {
      idTokenClaims: { oid: uuidv4 }
  }
  const projectId = 0

  const containers = await callApiWithToken(token, apiProjectContainers, account, "json", "POST",
      JSON.stringify({ id: projectId }), new Headers({ 'content-type': 'application/json' }))

  expect(containers).toMatchObject(expectedContainers);

  window.fetch.mockRestore();
});

test("apiCreateProject", async () => {
  const expectedProject = { id: 0, title: 'testTitle', description: 'testDesc', projectHolderId: 0, adminId: 0 }
  jest.spyOn(window, "fetch").mockImplementation(() => {
      const fetchResponse = {
          ok: true,
          json: () => Promise.resolve(expectedProject)
      };
      return Promise.resolve(fetchResponse);
  });

  const apiCreateProject = protectedResources.apiCreateProject.endpoint
  const token = uuidv4();
  const account = {
      idTokenClaims: { oid: uuidv4 }
  }

  const project = await callApiWithToken(token, apiCreateProject, account, "json", "POST",
      JSON.stringify(expectedProject), new Headers({ 'content-type': 'application/json' }))

  expect(project).toMatchObject(expectedProject);

  window.fetch.mockRestore();
});

test("apiUpdateProject", async () => {
  const originalProject = { id: 0, title: 'testTitle', description: 'testDesc', projectHolderId: 0, adminId: 0 }
  const updatedProject = { id: 0, title: 'updatedTitle', description: 'updatedDesc', projectHolderId: 0, adminId: 0 }
  jest.spyOn(window, "fetch").mockImplementation(() => {
      const fetchResponse = {
          ok: true,
          json: () => Promise.resolve(updatedProject)
      };
      return Promise.resolve(fetchResponse);
  });

  const apiUpdateProject = protectedResources.apiUpdateProject.endpoint
  const token = uuidv4();
  const account = {
      idTokenClaims: { oid: uuidv4 }
  }

  const project = await callApiWithToken(token, apiUpdateProject, account, "json", "POST",
      JSON.stringify({id: originalProject.id, title: 'updatedTitle', description: 'updatedDesc'}), new Headers({ 'content-type': 'application/json' }))

  expect(project).toMatchObject(updatedProject);

  window.fetch.mockRestore();
});

test("apiDeleteProject", async () => {
  const expectedProject = { id: 0, title: 'testTitle', description: 'testDesc', projectHolderId: 0, adminId: 0 }
  jest.spyOn(window, "fetch").mockImplementation(() => {
      const fetchResponse = {
          ok: true,
          json: () => Promise.resolve(expectedProject)
      };
      return Promise.resolve(fetchResponse);
  });

  const apiDeleteProject = protectedResources.apiDeleteProject.endpoint
  const token = uuidv4();
  const account = {
      idTokenClaims: { oid: uuidv4 }
  }
  const projectId = 0

  const project = await callApiWithToken(token, apiDeleteProject, account, "json", "POST",
      JSON.stringify({id: projectId}), new Headers({ 'content-type': 'application/json' }))

  expect(project).toMatchObject(expectedProject);

  window.fetch.mockRestore();
});

test("apiUpdateProjectUser", async () => {
  const originalProject = { id: 0, title: 'testTitle', description: 'testDesc', projectHolderId: 0, adminId: 0 }
  const updatedProject = { id: 0, title: 'testTitle', description: 'testDesc', projectHolderId: 1, adminId: 0 }
  jest.spyOn(window, "fetch").mockImplementation(() => {
      const fetchResponse = {
          ok: true,
          json: () => Promise.resolve(updatedProject)
      };
      return Promise.resolve(fetchResponse);
  });

  const apiUpdateProjectUser = protectedResources.apiUpdateProjectUser.endpoint
  const token = uuidv4();
  const account = {
      idTokenClaims: { oid: uuidv4 }
  }

  const project = await callApiWithToken(token, apiUpdateProjectUser, account, "json", "POST",
      JSON.stringify({id: originalProject.id, projectHolderId: 1}), new Headers({ 'content-type': 'application/json' }))

  expect(project).toMatchObject(updatedProject);

  window.fetch.mockRestore();
});

test("apiGetVideo", async () => {
  const expectedContainers = {
      id: 0, link: 'https://video.blob.com'
  }
  jest.spyOn(window, "fetch").mockImplementation(() => {
      const fetchResponse = {
          ok: true,
          json: () => Promise.resolve(expectedContainers)
      };
      return Promise.resolve(fetchResponse);
  });

  const apiProjectContainers = protectedResources.apiGetVideo.endpoint
  const token = uuidv4();
  const account = {
      idTokenClaims: { oid: uuidv4 }
  }
  const projectId = 0

  const containers = await callApiWithToken(token, apiProjectContainers, account, "json", "POST",
      JSON.stringify({ id: projectId }), new Headers({ 'content-type': 'application/json' }))

  expect(containers).toMatchObject(expectedContainers);

  window.fetch.mockRestore();
});

test("apiGetClasses", async () => {
  const expectedContainers = {
      id: 0, classes: [
          { value: 'aluminium', name: 'aluminium' },
          { value: 'film', name: 'film' },
          { value: 'hdpe_nat', name: 'hdpe_nat' },
          { value: 'hdpe_jazz', name: 'hdpe_jazz' },
          { value: 'paper', name: 'paper' },
          { value: 'pet_blue', name: 'pet_blue' },
          { value: 'pet_jazz', name: 'pet_jazz' },
          { value: 'pet_nat', name: 'pet_nat' },
          { value: 'pet_tray', name: 'pet_tray' },
          { value: 'pp', name: 'pp' },
          { value: 'residual', name: 'residual' },
          { value: 'steel', name: 'steel' },
          { value: 'wrapped_pet', name: 'wrapped_pet' },
      ]
  }
  jest.spyOn(window, "fetch").mockImplementation(() => {
      const fetchResponse = {
          ok: true,
          json: () => Promise.resolve(expectedContainers)
      };
      return Promise.resolve(fetchResponse);
  });

  const apiProjectContainers = protectedResources.apiGetClasses.endpoint
  const token = uuidv4();
  const account = {
      idTokenClaims: { oid: uuidv4 }
  }
  const projectId = 0

  const containers = await callApiWithToken(token, apiProjectContainers, account, "json", "POST",
      JSON.stringify({ id: projectId }), new Headers({ 'content-type': 'application/json' }))

  expect(containers).toMatchObject(expectedContainers);

  window.fetch.mockRestore();
});

test("apiGetCSV", async () => {
  const expectedContainers = {
      id: 0, csvData: [{ "id": 1701, "frameNum": 23, "objectId": 0, "label": "paper", "trackerL": 1027, "trackerT": 206, "trackerW": 379, "trackerH": 386, "modelConfidence": 0.6534, "trackerConfidence": 0.8484, "csvId": 9 },
      { "id": 1702, "frameNum": 31, "objectId": 4, "label": "residual", "trackerL": 754, "trackerT": 199, "trackerW": 175, "trackerH": 161, "modelConfidence": 0.9112, "trackerConfidence": 0.9384, "csvId": 9 },
      { "id": 1703, "frameNum": 37, "objectId": 9, "label": "hdpe_nat", "trackerL": 641, "trackerT": 530, "trackerW": 262, "trackerH": 442, "modelConfidence": 0.4168, "trackerConfidence": 1.0, "csvId": 9 },
      { "id": 1704, "frameNum": 40, "objectId": 12, "label": "film", "trackerL": 420, "trackerT": 233, "trackerW": 168, "trackerH": 250, "modelConfidence": 0.9321, "trackerConfidence": 1.0, "csvId": 9 },
      { "id": 1705, "frameNum": 40, "objectId": 10, "label": "hdpe_jazz", "trackerL": 1051, "trackerT": 230, "trackerW": 252, "trackerH": 564, "modelConfidence": 0.9541, "trackerConfidence": 0.8913, "csvId": 9 },
      { "id": 1706, "frameNum": 50, "objectId": 15, "label": "hdpe_jazz", "trackerL": 1094, "trackerT": 331, "trackerW": 254, "trackerH": 293, "modelConfidence": 0.9558, "trackerConfidence": 0.9445, "csvId": 9 },
      { "id": 1707, "frameNum": 52, "objectId": 16, "label": "film", "trackerL": 461, "trackerT": 194, "trackerW": 456, "trackerH": 180, "modelConfidence": 0.7453, "trackerConfidence": 0.9596, "csvId": 9 }]
  }
  jest.spyOn(window, "fetch").mockImplementation(() => {
      const fetchResponse = {
          ok: true,
          json: () => Promise.resolve(expectedContainers)
      };
      return Promise.resolve(fetchResponse);
  });

  const apiProjectContainers = protectedResources.apiGetCsv.endpoint
  const token = uuidv4();
  const account = {
      idTokenClaims: { oid: uuidv4 }
  }
  const projectId = 0

  const containers = await callApiWithToken(token, apiProjectContainers, account, "json", "POST",
      JSON.stringify({ id: projectId }), new Headers({ 'content-type': 'application/json' }))

  expect(containers).toMatchObject(expectedContainers);

  window.fetch.mockRestore();
});

test("apiGetPersistentCSV", async () => {
  const expectedContainers = {
      id: 0, csvData: [{ "id": 1701, "frameNum": 23, "objectId": 0, "label": "paper", "trackerL": 1027, "trackerT": 206, "trackerW": 379, "trackerH": 386, "modelConfidence": 0.6534, "trackerConfidence": 0.8484, "csvId": 9 },
      { "id": 1703, "frameNum": 37, "objectId": 9, "label": "hdpe_nat", "trackerL": 641, "trackerT": 530, "trackerW": 262, "trackerH": 442, "modelConfidence": 0.4168, "trackerConfidence": 1.0, "csvId": 9 },
      { "id": 1705, "frameNum": 40, "objectId": 10, "label": "hdpe_jazz", "trackerL": 1051, "trackerT": 230, "trackerW": 252, "trackerH": 564, "modelConfidence": 0.9541, "trackerConfidence": 0.8913, "csvId": 9 },
      { "id": 1707, "frameNum": 52, "objectId": 16, "label": "film", "trackerL": 461, "trackerT": 194, "trackerW": 456, "trackerH": 180, "modelConfidence": 0.7453, "trackerConfidence": 0.9596, "csvId": 9 }]
  }
  jest.spyOn(window, "fetch").mockImplementation(() => {
      const fetchResponse = {
          ok: true,
          json: () => Promise.resolve(expectedContainers)
      };
      return Promise.resolve(fetchResponse);
  });

  const apiProjectContainers = protectedResources.apiGetCsv.endpoint
  const token = uuidv4();
  const account = {
      idTokenClaims: { oid: uuidv4 }
  }
  const projectId = 0

  const containers = await callApiWithToken(token, apiProjectContainers, account, "json", "POST",
      JSON.stringify({ id: projectId }), new Headers({ 'content-type': 'application/json' }))

  expect(containers).toMatchObject(expectedContainers);

  window.fetch.mockRestore();
});

test("apiListBlobs", async () => {
  const expectedContainers = {
      id: 0, blobs: [{link: 'https://recycleye.mp4.blob.com/3ojlk12j3oijfa'}, {link: 'https://recycleye.mp4.blob.com/jfeq94312huifdas'}]}
  jest.spyOn(window, "fetch").mockImplementation(() => {
      const fetchResponse = {
          ok: true,
          json: () => Promise.resolve(expectedContainers)
      };
      return Promise.resolve(fetchResponse);
  });

  const apiProjectContainers = protectedResources.apiListBlobs.endpoint
  const token = uuidv4();
  const account = {
      idTokenClaims: { oid: uuidv4 }
  }
  const projectId = 0

  const containers = await callApiWithToken(token, apiProjectContainers, account, "json", "POST",
      JSON.stringify({ id: projectId }), new Headers({ 'content-type': 'application/json' }))

  expect(containers).toMatchObject(expectedContainers);

  window.fetch.mockRestore();
});

test("apiSaveCSV", async () => {
    const expectedContainers = {
        id: 0, csvData: [{ "id": 1701, "frameNum": 23, "objectId": 0, "label": "paper", "trackerL": 1027, "trackerT": 206, "trackerW": 379, "trackerH": 386, "modelConfidence": 0.6534, "trackerConfidence": 0.8484, "csvId": 9 },
        { "id": 1703, "frameNum": 37, "objectId": 9, "label": "hdpe_nat", "trackerL": 641, "trackerT": 530, "trackerW": 262, "trackerH": 442, "modelConfidence": 0.4168, "trackerConfidence": 1.0, "csvId": 9 },
        { "id": 1705, "frameNum": 40, "objectId": 10, "label": "hdpe_jazz", "trackerL": 1051, "trackerT": 230, "trackerW": 252, "trackerH": 564, "modelConfidence": 0.9541, "trackerConfidence": 0.8913, "csvId": 9 },
        { "id": 1707, "frameNum": 52, "objectId": 16, "label": "film", "trackerL": 461, "trackerT": 194, "trackerW": 456, "trackerH": 180, "modelConfidence": 0.7453, "trackerConfidence": 0.9596, "csvId": 9 }]
    }
    const response = {message: 'received'}
    jest.spyOn(window, "fetch").mockImplementation(() => {
        const fetchResponse = {
            ok: true,
            json: () => Promise.resolve(response)
        };
        return Promise.resolve(fetchResponse);
    });

    const apiProjectContainers = protectedResources.apiSaveCsv.endpoint
    const token = uuidv4();
    const account = {
        idTokenClaims: { oid: uuidv4 }
    }
    const projectId = 0

    const containers = await callApiWithToken(token, apiProjectContainers, account, "json", "POST",
        JSON.stringify(expectedContainers), new Headers({ 'content-type': 'application/json' }))

    expect(containers).toMatchObject(response);

    window.fetch.mockRestore();
});

test("apiSubmit", async () => {
    const expectedContainers = {
        id: 0, csvData: [{ "id": 1701, "frameNum": 23, "objectId": 0, "label": "paper", "trackerL": 1027, "trackerT": 206, "trackerW": 379, "trackerH": 386, "modelConfidence": 0.6534, "trackerConfidence": 0.8484, "csvId": 9 },
        { "id": 1703, "frameNum": 37, "objectId": 9, "label": "hdpe_nat", "trackerL": 641, "trackerT": 530, "trackerW": 262, "trackerH": 442, "modelConfidence": 0.4168, "trackerConfidence": 1.0, "csvId": 9 },
        { "id": 1705, "frameNum": 40, "objectId": 10, "label": "hdpe_jazz", "trackerL": 1051, "trackerT": 230, "trackerW": 252, "trackerH": 564, "modelConfidence": 0.9541, "trackerConfidence": 0.8913, "csvId": 9 },
        { "id": 1707, "frameNum": 52, "objectId": 16, "label": "film", "trackerL": 461, "trackerT": 194, "trackerW": 456, "trackerH": 180, "modelConfidence": 0.7453, "trackerConfidence": 0.9596, "csvId": 9 }]
    }
    const response = {message: 'received'}
    jest.spyOn(window, "fetch").mockImplementation(() => {
        const fetchResponse = {
            ok: true,
            json: () => Promise.resolve(response)
        };
        return Promise.resolve(fetchResponse);
    });

    const apiProjectContainers = protectedResources.apiSubmit.endpoint
    const token = uuidv4();
    const account = {
        idTokenClaims: { oid: uuidv4 }
    }
    const projectId = 0

    const containers = await callApiWithToken(token, apiProjectContainers, account, "json", "POST",
        JSON.stringify(expectedContainers), new Headers({ 'content-type': 'application/json' }))

    expect(containers).toMatchObject(response);

    window.fetch.mockRestore();
});