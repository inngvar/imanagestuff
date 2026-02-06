import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import { InputGroup, InputGroupAddon, Button, Input, Label } from 'reactstrap';

export const ProjectList = props => {
  const [defaultProject, setDefaultProjects] = useState(null);

  const onChange = event => {
    const te = props.projects.find(p => p.id.toString() === event.target.value);
    props.handler(te);
  };

  useEffect(() => {
    if (props?.value) {
      setDefaultProjects(props.value);
    }
  });

  return (
    <div className="col-md-6">
      <Label for="project">Проект</Label>
      <InputGroup>
        {props?.showButton ? (
          <InputGroupAddon addonType="prepend">
            <Button color="info" disabled={props.isDefaultProject} onClick={props.updateDefaultProject}>
              По умолчанию
            </Button>
          </InputGroupAddon>
        ) : (
          ''
        )}
        <Input type="select" name="project" id="project" onChange={onChange}>
          {props.projects.map((project, i) =>
            project === defaultProject ? (
              <option key={i} value={project.id} selected>
                {project.name}
              </option>
            ) : (
              <option key={i} value={project.id}>
                {project.name}
              </option>
            )
          )}
        </Input>
      </InputGroup>
    </div>
  );
};

ProjectList.propTypes = {
  isDefaultProject: PropTypes.bool,
  updateDefaultProject: PropTypes.func,
  projects: PropTypes.arrayOf(PropTypes.object),
  value: PropTypes.object,
  handler: PropTypes.func,
  showButton: PropTypes.bool,
};
